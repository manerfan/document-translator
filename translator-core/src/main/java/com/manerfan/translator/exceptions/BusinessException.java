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

import java.util.Optional;

/**
 * Created by manerfan on 2017/10/12.
 */

public class BusinessException extends RuntimeException {
    ErrorCode errorCode;

    public BusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public BusinessException(ErrorCode errorCode, Optional<Object> message) {
        super(message.orElse("null").toString());
        this.errorCode = errorCode;
    }

    public BusinessException(ErrorCode errorCode, Optional<Object> message, Throwable cause) {
        super(message.orElse("null").toString(), cause);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
