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

package com.manerfan.translator.web.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.manerfan.translator.exceptions.BusinessException;
import com.manerfan.translator.exceptions.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

/**
 * ControllerAdvicer
 *
 * @author manerfan
 * @date 2017/10/12
 */

@RestControllerAdvice
public class ControllerAdvicer {
    @Autowired
    ObjectMapper mapper;

    Logger accessLogger = LoggerFactory.getLogger("access");
    Logger logger = LoggerFactory.getLogger(ControllerAdvice.class);

    /**
     * 404异常
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public Object noHandler404(NoHandlerFoundException ex, HttpServletRequest request, HttpServletResponse resp) {
        accessLogger.info("{} {} {}", request.getMethod(), request.getRequestURI(), request.getProtocol());
        return response(HttpStatus.NOT_FOUND, Optional.ofNullable(ex.getMessage()), resp);
    }

    /**
     * 业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public ObjectNode businessError(BusinessException ex, HttpServletResponse resp) {
        logger.error("Business Error {}", ex.getMessage());
        return response(ex.getErrorCode(), ex.getMessage(), resp);
    }

    /**
     * 参数错误
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ObjectNode illegalArgument(IllegalArgumentException ex, HttpServletResponse resp) {
        logger.error("Params Error {}", ex.getMessage());
        return response(ErrorCode.PARAMS_INVALID, ex.getMessage(), resp);
    }

    /**
     * 内部异常
     */
    @ExceptionHandler(Throwable.class)
    public ObjectNode internalServerError500(Throwable ex, HttpServletResponse resp) {
        logger.error("Internal Error {}", ex.getMessage(), ex);
        return response(HttpStatus.INTERNAL_SERVER_ERROR, Optional.ofNullable(ex.getMessage()), resp);
    }

    private ObjectNode response(HttpStatus status, Optional<String> message, HttpServletResponse resp) {
        ObjectNode node = mapper.createObjectNode();
        node.put("code", status.value());
        node.put("message", message.orElse(status.getReasonPhrase()));
        resp.setStatus(status.value());
        return node;
    }

    private ObjectNode response(int statusCode, int code, Optional<String> message, HttpServletResponse resp) {
        ObjectNode node = mapper.createObjectNode();
        node.put("code", code);
        node.put("message", message.orElse(""));
        resp.setStatus(statusCode);
        return node;
    }

    private ObjectNode response(ErrorCode errorCode, String message, HttpServletResponse resp) {
        return response(errorCode.getStatusCode(), errorCode.getCode(), Optional.ofNullable(message), resp);
    }
}
