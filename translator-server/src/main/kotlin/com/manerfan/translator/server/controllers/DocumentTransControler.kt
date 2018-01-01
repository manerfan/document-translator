package com.manerfan.translator.server.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.common.io.Files
import com.google.common.io.Files.getFileExtension
import com.google.common.io.Files.getNameWithoutExtension
import com.manerfan.translator.BusinessException
import com.manerfan.translator.ErrorCode
import com.manerfan.translator.api.baidu.Md5
import com.manerfan.translator.api.baidu.TranslatorManager
import com.manerfan.translator.api.document.DocumentTransManager
import com.manerfan.translator.api.sbd.SentenceSegmentation
import com.manerfan.translator.data.FileEntity
import com.manerfan.translator.data.FileRepository
import com.manerfan.translator.data.StatisticsEntity
import com.manerfan.translator.data.StatisticsRepository
import org.apache.commons.io.FileUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.util.StringUtils
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.*


/**
 * @author manerfan
 * @date 2018/1/1
 */

@RestController
@RequestMapping("/api/trans")
class DocumentTransControler {
    @Autowired
    lateinit var transManager: TranslatorManager

    @Autowired
    lateinit var statisticsRepository: StatisticsRepository

    @Autowired
    lateinit var sentenceSegmentation: SentenceSegmentation

    private val TMP_PREFIX = "doctrans_"

    @Autowired
    lateinit var md5: Md5

    @Autowired
    lateinit var documentTransManager: DocumentTransManager

    @Autowired
    lateinit var fileRepository: FileRepository

    @Value("\${server.data.doc.dir}")
    lateinit var docDir: File

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @RequestMapping("/text")
    @PostMapping(consumes = [MediaType.APPLICATION_FORM_URLENCODED_VALUE])
    @Throws(Exception::class)
    fun trans(@RequestParam q: String,
              @RequestParam(required = false, defaultValue = "auto") from: String,
              @RequestParam to: String): Map<String, Any> {
        return mapOf(
                "code" to 200,
                "message" to "success",
                "data" to transManager.transText(q, from, to)
        )
    }

    @PostMapping("/sbd")
    fun sentenceBreak(@RequestParam q: String): Map<String, Any> {
        return mapOf(
                "code" to 200,
                "message" to "success",
                "data" to sentenceSegmentation.segment(q).toStringList()
        )
    }

    @PostMapping("/doc")
    fun trans(@RequestParam("file") file: MultipartFile,
              @RequestParam(name = "from", required = false, defaultValue = "auto") from: String,
              @RequestParam to: String): Map<String, Any> {
        val srcFilename = file.originalFilename ?: "uk"
        val key = md5.encrypt(UUID.randomUUID().toString())
        val srcDoc = File(docDir, "$TMP_PREFIX$key.${getFileExtension(srcFilename) ?: "uk"}")
        file.transferTo(srcDoc)
        fileRepository.save(FileEntity(name = srcDoc.name, language = from))

        val destFile = documentTransManager.translate(srcDoc.absolutePath, from, to)
        val fileEntity =  fileRepository.save(FileEntity(name = StringUtils.getFilename(destFile) ?: "uk", language = to))

        return mapOf(
                "code" to 200,
                "message" to "success",
                "data" to mapOf(
                        "from" to from,
                        "to" to to,
                        "key" to fileEntity.uuid,
                        "filename" to "${getNameWithoutExtension(srcFilename) ?: "uk"}_$to.${getFileExtension(srcFilename) ?: "uk"}"
                )
        )
    }

    @GetMapping("/doc/{key}")
    fun download(@PathVariable key: String, @RequestParam filename: String): ResponseEntity<ByteArray> {
        var file = fileRepository.findById(key).map { File(docDir, it.name) }.orElseThrow { BusinessException(ErrorCode.RESOURCE_MISSING, "Resource Not Found! $key") }
        var headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_OCTET_STREAM
        headers.setContentDispositionFormData("attachment", URLEncoder.encode(filename, StandardCharsets.UTF_8.name()))

        return ResponseEntity(FileUtils.readFileToByteArray(file), headers, HttpStatus.OK)
    }

    @GetMapping("/statistics")
    fun statistics(): Map<String, Any> {
        return mapOf(
                "code" to 200,
                "message" to "success",
                "data" to (statisticsRepository.getStatistics() ?: StatisticsEntity())
        )
    }
}