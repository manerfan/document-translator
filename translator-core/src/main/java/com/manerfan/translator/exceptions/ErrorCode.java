/*
 *
 *  * ManerFan(http://manerfan.com). All Rights Reserved.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package com.manerfan.translator.exceptions;

/**
 * Created by manerfan on 2017/10/12.
 */

public enum ErrorCode {
    /**
     * 未知错误
     */
    UNKNOW_ERROR(500, 500),

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
    REST_SERVICE_ERROR(400, 410);

    int statusCode;
    int code;

    ErrorCode(int statusCode, int code) {
        this.statusCode = statusCode;
        this.code = code;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public int getCode() {
        return statusCode * 1000 + code;
    }
}
