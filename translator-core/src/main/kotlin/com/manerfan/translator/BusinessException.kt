/*
 * ManerFan(http://manerfan.com). All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.manerfan.translator

/**
 * @author manerfan
 * @date 2017/12/4
 */

class BusinessException(val errorCode: ErrorCode = ErrorCode.UNKNOWN_ERROR, message: String? = null, throwable: Throwable? = null)
    : RuntimeException(message ?: errorCode.code.toString(), throwable)

enum class ErrorCode(val statusCode: Int, code: Int) {
    /**
     * 未知错误
     */
    UNKNOWN_ERROR(500, 500),

    /**
     * 参数缺失
     */
    PARAMS_MISSING(400, 401),

    /**
     * 参数错误
     */
    PARAMS_INVALID(400, 402),

    /**
     * 资源缺失
     */
    RESOURCE_MISSING(400, 403),

    /**
     * REST接口错误
     */
    REST_SERVICE_ERROR(400, 410),

    /**
     * 404
     */
    NOT_FOUND(404, 404);

    var code = code
        private set
        get() = statusCode * 1000 + field
}