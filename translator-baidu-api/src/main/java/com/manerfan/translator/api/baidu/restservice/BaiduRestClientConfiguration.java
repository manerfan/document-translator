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

import com.manerfan.translator.feignconfigurations.FormUrlEncodedClientConfiguration;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;

/**
 * @author manerfan
 * @date 2017/10/31
 */

public class BaiduRestClientConfiguration extends FormUrlEncodedClientConfiguration {
    @Bean
    ErrorDecoder errorDecoder() {
        return new BaiduErrorDecode();
    }
}
