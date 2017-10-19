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

import com.manerfan.translator.api.baidu.body.BaiduResponseBody;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Map;

/**
 * @author manerfan
 * @date 2017/10/19
 */

@FeignClient(
        name = "baidu-trans",
        url = "${baidu.trans.url:https://fanyi-api.baidu.com/api/transText/vip/}",
        configuration = BaiduRestClientConfiguration.class
)
public interface BaiduRestService {
    /**
     * 调用百度翻译接口
     *
     * @param params 参数
     *
     * @return
     */
    @PostMapping(path = "translate", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    BaiduResponseBody translate(Map<String, ?> params);
}
