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

package com.manerfan.translator.server.interceptors

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * @author manerfan
 * @date 2018/1/1
 */

val HttpServletRequest.remoteIp: String
    get() {
        var xff = this.getHeader("x-forwarded-for")
        var xri = this.getHeader("x-real-ip")

        if (!xff.isNullOrEmpty() && "unknown" != xff.toLowerCase()) {
            return xff.split(",")[0].trim()
        }

        if (!xri.isNullOrEmpty() && "unknown" != xri.toLowerCase()) {
            return xri
        }

        return this.remoteAddr
    }

@Component
class AccessLogInterceptor : HandlerInterceptorAdapter() {
    private val logger = LoggerFactory.getLogger(AccessLogInterceptor::class.java)

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any?): Boolean {
        logger.info("[access] {} {} {}", request.remoteIp, request.method, request.requestURI)
        return true
    }

}
