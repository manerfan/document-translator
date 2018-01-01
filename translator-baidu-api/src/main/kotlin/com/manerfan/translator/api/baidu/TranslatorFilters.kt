/*
 * ManerFan(http://manerfan.com). All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.manerfan.translator.api.baidu

import com.google.common.collect.Maps
import com.google.common.collect.Sets
import org.springframework.util.StringUtils
import java.util.*
import java.util.regex.Pattern


/**
 * @author manerfan
 * @date 2017/12/5
 */

class FilterContext<out T> {
    /**
     * 使用ThreadLocal，重构为RxJava时会有风险
     */
    private var context = Maps.newHashMap<String, Map<String, T>>()

    private fun checkAndInit(id: String): Map<String, T> {
        if (!context.contains(id)) {
            context.put(id, Maps.newHashMap())
        }

        return context[id]!!
    }

    operator fun get(id: String) = checkAndInit(id)
}

interface TranslatorFilter<in T> {
    fun pre(q: String, filterContext: FilterContext<T>): String
    fun post(body: BaiduResponseBody, filterContext: FilterContext<T>): BaiduResponseBody
}

class TrimFilter : TranslatorFilter<String> {
    override fun pre(q: String, filterContext: FilterContext<String>) = q

    override fun post(body: BaiduResponseBody, filterContext: FilterContext<String>): BaiduResponseBody {
        val rplPattern = "[\\s]+".toRegex()
        body.trans_result.orEmpty().forEach {
            it.src = it.src.replace(rplPattern, " ").trim()
            it.dst = it.dst.replace(rplPattern, " ").trim()
        }
        return body
    }
}

class RegexFilter(pattern: String, private val prefix: String = "", private val suffix: String = "") : TranslatorFilter<String> {
    private val uuid = UUID.randomUUID().toString()
    private val pattern = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE)

    /**
     * 列出q中符合pattern的子字符串
     */
    private fun symbols(q: String): Set<String> {
        val groups = Sets.newHashSet<String>()
        val matcher = pattern.matcher(q)
        while (matcher.find()) {
            groups.add(matcher.group())
        }

        return groups
    }

    // 00[\d]{6} 目前只发现这种格式可以不被百度误认为为匈牙利语等小语种
    private fun symbolId() = (Math.abs(UUID.randomUUID().hashCode()) % 1000000).toString().padStart(8, '0')

    /**
     * key: symbol id
     * value: 符合pattern的子字符串
     */
    private fun group(q: String): Map<String, String> {
        val ids = mutableMapOf<String, String>()
        symbols(q).forEach { symbol -> ids.put(symbolId(), symbol) }
        return ids
    }

    override fun pre(q: String, filterContext: FilterContext<String>): String {
        var query = q
        val groups = group(query)
        filterContext[uuid].plus(groups)
        groups.forEach { key, value -> query = StringUtils.replace(query, value, key) }
        return query
    }

    private fun restore(body: BaiduTransBody, filterContext: FilterContext<String>) {
        filterContext[uuid].forEach { key, value ->
            val replaceRegex = "(?i)$key".toRegex()
            val replacement = "$prefix$value$suffix"
            body.dst = body.dst.replace(replaceRegex, replacement)
            body.src = body.src.replace(replaceRegex, replacement)
        }
    }

    override fun post(body: BaiduResponseBody, filterContext: FilterContext<String>): BaiduResponseBody {
        body.trans_result.orEmpty().forEach { restore(it, filterContext) }
        return body
    }
}

