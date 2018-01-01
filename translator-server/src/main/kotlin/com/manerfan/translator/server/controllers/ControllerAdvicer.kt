package com.manerfan.translator.server.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import com.manerfan.translator.BusinessException
import com.manerfan.translator.ErrorCode
import com.manerfan.translator.server.interceptors.remoteIp
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.servlet.NoHandlerFoundException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * @author manerfan
 * @date 2018/1/1
 */

@RestControllerAdvice
class ControllerAdvicer {
    @Autowired
    lateinit var mapper: ObjectMapper

    private val logger = LoggerFactory.getLogger(ControllerAdvice::class.java)

    /**
     * 404异常
     */
    @ExceptionHandler(NoHandlerFoundException::class)
    fun noHandler404(ex: NoHandlerFoundException, request: HttpServletRequest, resp: HttpServletResponse): ObjectNode {
        logger.warn("[404 error] {} {} {}", request.remoteIp, request.method, request.requestURI)
        return response(HttpStatus.NOT_FOUND, ex.message, resp)
    }

    @ExceptionHandler(HttpClientErrorException::class)
    fun clientError(ex: HttpClientErrorException, request: HttpServletRequest, resp: HttpServletResponse): ObjectNode {
        logger.info("[http client error] {} {} {}", request.remoteIp, request.method, request.requestURI)
        return response(ex.statusCode, ex.message, resp)
    }

    /**
     * 业务异常
     */
    @ExceptionHandler(BusinessException::class)
    fun businessError(ex: BusinessException, request: HttpServletRequest, resp: HttpServletResponse): ObjectNode {
        logger.error("[business error] {} {} {}", request.remoteIp, request.method, request.requestURI, ex)
        return response(ex.errorCode, ex.message, resp)
    }

    /**
     * 参数错误
     */
    @ExceptionHandler(IllegalArgumentException::class)
    fun illegalArgument(ex: IllegalArgumentException, resp: HttpServletResponse): ObjectNode {
        return response(ErrorCode.PARAMS_INVALID, ex.message, resp)
    }

    /**
     * 内部异常
     */
    @ExceptionHandler(Throwable::class)
    fun internalServerError500(ex: Throwable, request: HttpServletRequest, resp: HttpServletResponse): ObjectNode {
        logger.error("[500 error] {} {} {}", request.remoteIp, request.method, request.requestURI, ex)
        return response(HttpStatus.INTERNAL_SERVER_ERROR, ex.message, resp)
    }

    private fun response(status: HttpStatus, message: String?, resp: HttpServletResponse): ObjectNode {
        val node = mapper.createObjectNode()
        node.put("code", status.value())
        node.put("message", message ?: status.reasonPhrase)
        resp.status = status.value()
        return node
    }

    private fun response(statusCode: Int, code: Int, message: String?, resp: HttpServletResponse): ObjectNode {
        val node = mapper.createObjectNode()
        node.put("code", code)
        node.put("message", message.orEmpty())
        resp.status = statusCode
        return node
    }

    private fun response(errorCode: ErrorCode, message: String?, resp: HttpServletResponse): ObjectNode {
        return response(errorCode.statusCode, errorCode.code, message, resp)
    }
}