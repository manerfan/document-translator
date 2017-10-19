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

package com.manerfan.translator.api.baidu.restservice;

import com.manerfan.translator.exceptions.BusinessException;
import com.manerfan.translator.exceptions.ErrorCode;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import javax.ws.rs.WebApplicationException;
import java.io.IOException;

/**
 * @author manerfan
 * @date 2017/10/31
 */

public class BaiduErrorDecode implements ErrorDecoder {
    Logger logger = LoggerFactory.getLogger(BaiduErrorDecode.class);

    @Override
    public Exception decode(String methodKey, Response response) {
        Exception ex = new WebApplicationException(response.status());

        try {
            String body = IOUtils.toString(response.body().asReader());
            if (HttpStatus.OK.value() != response.status()) {
                ex = new BusinessException(ErrorCode.REST_SERVICE_ERROR, body);
            }
        } catch (IOException e) {
            logger.error("Read Baidu Response Error!", e);
        }

        return ex;
    }
}
