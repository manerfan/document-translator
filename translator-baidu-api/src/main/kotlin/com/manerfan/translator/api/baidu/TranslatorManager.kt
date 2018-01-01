package com.manerfan.translator.api.baidu

import com.google.common.base.Preconditions
import com.google.common.collect.Lists
import com.manerfan.translator.BusinessException
import com.manerfan.translator.ErrorCode
import com.manerfan.translator.api.sbd.SentenceSegmentation
import com.manerfan.translator.data.StatisticsRepository
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.runBlocking
import org.apache.commons.pool2.BasePooledObjectFactory
import org.apache.commons.pool2.impl.DefaultPooledObject
import org.apache.commons.pool2.impl.GenericObjectPool
import org.apache.commons.pool2.impl.GenericObjectPoolConfig
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.math.BigInteger
import java.security.MessageDigest

/**
 * @author manerfan
 * @date 2017/12/6
 */

@Component
class Md5 {
    private lateinit var md5DigestPool: GenericObjectPool<MessageDigest>

    fun encrypt(str: String): String {
        val md5 = md5DigestPool.borrowObject()
        try {
            md5.reset()
            md5.update(str.toByteArray())
            return BigInteger(1, md5.digest()).toString(16).padStart(32, '0')
        } finally {
            md5DigestPool.returnObject(md5)
        }
    }

    private fun md5DigestPoolFactory(): BasePooledObjectFactory<MessageDigest> {
        return object : BasePooledObjectFactory<MessageDigest>() {
            override fun create() = MessageDigest.getInstance("MD5")
            override fun wrap(messageDigest: MessageDigest) = DefaultPooledObject(messageDigest)
        }
    }

    init {
        val poolConfig = GenericObjectPoolConfig()

        /* 设置池中最大idle，若idle数量大于此值，则会清理多余idle */
        poolConfig.maxIdle = 100
        /* 设置多久没有borrow则设置为idle */
        poolConfig.minEvictableIdleTimeMillis = (5 * 60 * 1000).toLong()

        /* 设置池中最小idle，若idle数量小于此值，则在Evictor定时器中会自动创建idle */
        poolConfig.minIdle = 50
        /* 设置Evictor定时器周期并启动定时器 */
        poolConfig.timeBetweenEvictionRunsMillis = (30 * 1000).toLong()

        /* 设置池中最大数量，若达到上限时borrow，则阻塞 */
        poolConfig.maxTotal = 500

        /* 调用者最大阻塞的时间 */
        poolConfig.maxWaitMillis = (5 * 1000).toLong()

        md5DigestPool = GenericObjectPool(md5DigestPoolFactory(), poolConfig)
        // 初始化池中idle个数到minIdle，提前create，免得在使用时再创建
        // BaseGenericObjectPool.Evictor定时器会定时执行ensureMinIdle，确保idle keypare个数可以达到minIdle
        md5DigestPool.preparePool()
    }
}

@Component
class TranslatorManager(
        val md5: Md5,
        val restService: RestService,
        val sentenceSegmentation: SentenceSegmentation,
        val filters: List<TranslatorFilter<String>>
) {
    private val logger = LoggerFactory.getLogger(TranslatorManager::class.java)

    @Value("\${baidu.trans.app.id}")
    lateinit var appId: String

    @Value("\${baidu.trans.app.secret}")
    private lateinit var appSecret: String

    @Value("\${baidu.trans.limit:5000}")
    private var limit: Int = 0

    @Autowired
    lateinit var statisticsRepository: StatisticsRepository

    /**
     * 生成签名
     */
    private fun sign(q: String, salt: Long) = md5.encrypt("$appId$q$salt$appSecret").padStart(32, '0')

    /**
     * 翻译
     */
    private fun translate(q: String, from: String = "auto", to: String = "zh"): BaiduResponseBody {
        Preconditions.checkArgument(q.length <= limit, "q is too long (${q.length}) than $limit bytes")

        val salt = System.currentTimeMillis()
        var resp = restService.translate(q, from, to, appId, salt, sign(q, salt)).execute()
        if (200 != resp.code()) {
            throw BusinessException(ErrorCode.REST_SERVICE_ERROR, resp.body()?.toString())
        }

        var body = resp.body() ?: throw BusinessException(ErrorCode.REST_SERVICE_ERROR, "Baidu Translator Api Response Empty!")
        body.error_code?.let { throw BusinessException(ErrorCode.REST_SERVICE_ERROR, resp.body()?.toString()) }

        return body
    }

    /**
     * 短文本翻译
     */
    fun transText(q: String, from: String, to: String): BaiduResponseBody {
        var query = q
        var context = FilterContext<String>()

        /* 预处理 */
        filters.forEach { query = it.pre(query, context) }

        /* 翻译 */
        var response = translate(query, from, to)

        /* 后处理 */
        filters.forEach { response = it.post(response, context) }

        statisticsRepository.increaseTextTimes()
        statisticsRepository.increaseBytesNum(q.length.toLong())

        return response
    }

    /**
     * 批量短文本翻译
     */
    fun transTextArray(qs: Collection<String>, from: String, to: String): List<BaiduResponseBody> {
        return runBlocking { qs.map { async(CommonPool) { transText(it, from, to) } }.map { it.await() } }
    }

    /**
     * 将长文本拆分为短文本列表
     */
    private fun fixedToSentences(sentence: String): List<String> {
        var segments = sentenceSegmentation.segment(sentence)

        var sb = StringBuilder()
        var sentences = Lists.newLinkedList<String>()

        for (segment in segments) {
            if (sb.length + segment.length > limit) {
                sentences.add(sb.toString())
                sb = StringBuilder()
            }
            sb.append(segment)
        }
        sentences.add(sb.toString())

        return sentences
    }

    /**
     * 长文本翻译（忽略回车符）
     */
    fun transSection(s: String, from: String, to: String): BaiduResponseBody {
        var dst = transTextArray(fixedToSentences(s), from, to)
                .flatMap { it.trans_result.orEmpty() }.joinToString("") { it.dst }

        return BaiduResponseBody(from, to, listOf(BaiduTransBody(s, dst)))
    }

    /**
     * 批量翻译句子/段落（忽略每一条子数据中的回车符）
     */
    fun transSectionArray(sentences: Collection<String>, from: String, to: String): List<String> {
        return groupWrapperList(sentences).onEach {
            when {
                it.size > 1 -> transTextArray(it.map(SentenceWrapper::src), from, to).forEachIndexed { index, baiduResponseBody ->
                    it[index].dst = baiduResponseBody.trans_result.orEmpty().joinToString("") { it.dst }
                }
                else -> it[0].dst = transSection(it[0].src, from, to).trans_result.orEmpty().joinToString("") { it.dst }
            }
        }.flatMap { it.map(SentenceWrapper::dst) }
    }

    private fun groupWrapperList(sentences: Collection<String>): List<List<SentenceWrapper>> {
        var groupWrappers = mutableListOf<List<SentenceWrapper>>()
        var wrappers = mutableListOf<SentenceWrapper>()

        var len = 0
        sentences.filter(String::isNotBlank).forEach {
            if (len + it.length > limit) {
                groupWrappers.add(wrappers)
                wrappers = mutableListOf()
                len = 0
            }

            len += it.length
            wrappers.add(SentenceWrapper(it))
        }
        groupWrappers.add(wrappers)
        return groupWrappers
    }

    private inner class SentenceWrapper(var src: String) {
        private var preBlankPattern = "^[\\s]+".toPattern()

        var prefix = ""
        var dst: String
            get() = prefix + field

        init {
            var matcher = preBlankPattern.matcher(src)
            if (matcher.find()) {
                prefix = matcher.group()
                src = matcher.replaceFirst("")
            }
            dst = src
        }
    }
}
