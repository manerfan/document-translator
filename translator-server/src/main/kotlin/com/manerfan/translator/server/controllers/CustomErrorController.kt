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

package com.manerfan.translator.server.controllers

import com.manerfan.translator.BusinessException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.web.servlet.error.ErrorController
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest

/**
 * @author manerfan
 * @date 2017/12/25
 */

//@RestController
class CustomErrorController : ErrorController {
    private val logger = LoggerFactory.getLogger(CustomErrorController::class.java)

    @Value("\${server.error.path:\${error.path:/error}}")
    lateinit var errPath: String

    override fun getErrorPath() = errPath

    @RequestMapping("\${server.error.path:\${error.path:/error}}")
    fun error(request: HttpServletRequest): ResponseEntity<Any> {
        var statusCode = request.getAttribute("javax.servlet.error.status_code") as? Int ?: 400
        var message = request.getAttribute("javax.servlet.error.message") as? String ?: "You should not call this interface forwardly"
        var exception = request.getAttribute("javax.servlet.error.exception") as? Throwable ?: BusinessException()

        logger.error("[{}] {}", statusCode, message, exception)

        return ResponseEntity.status(statusCode).body(mapOf(
                "code" to statusCode,
                "message" to message
        ))
    }
}