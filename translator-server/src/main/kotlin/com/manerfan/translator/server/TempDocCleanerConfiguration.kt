package com.manerfan.translator.server

import com.manerfan.translator.data.FileRepository
import java.io.File
import org.slf4j.LoggerFactory
import java.time.ZoneId
import java.time.LocalDateTime
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.scheduling.annotation.Async
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.util.*


/**
 * @author manerfan
 * @date 2018/1/1
 */

@Component
@ConditionalOnProperty(value = ["server.data.doc.clean.enabled"], matchIfMissing = true)
class TempDocCleanerConfiguration {
    private val logger = LoggerFactory.getLogger(TempDocCleanerConfiguration::class.java)

    @Autowired
    lateinit var fileRepository: FileRepository

    @Value("\${server.data.doc.dir}")
    lateinit var docDir: File

    @Async
    @Scheduled(cron = "0 0 2 * * ?")
    fun clean() {
        val limit = Date.from(LocalDateTime.now().plusDays(-1).atZone(ZoneId.systemDefault()).toInstant())

        try {
            fileRepository.findBefore(limit)
                    .parallelStream()
                    .map { File(docDir, it.name) }
                    .filter { it.exists() && it.isFile }
                    .forEach { it.delete() }
            fileRepository.deleteBefore(limit)
        } catch (e: Exception) {
            logger.error("TempDocCleaner Schedule Error!", e)
        }

    }
}