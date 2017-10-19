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

package com.manerfan.translator.api.baidu.filters;

import com.manerfan.translator.api.baidu.body.BaiduResponseBody;
import org.springframework.util.CollectionUtils;

import java.util.Optional;

/**
 * @author manerfan
 * @date 2017/10/22
 */

public class TrimFilter implements TransFilter<String> {
    @Override
    public String pre(String q, FilterContext<String> filterContext) {
        return q;
    }

    @Override
    public BaiduResponseBody post(BaiduResponseBody body, FilterContext<String> filterContext) {
        Optional.ofNullable(body.getTrans_result())
                .filter((transResults) -> !CollectionUtils.isEmpty(transResults))
                .ifPresent((transResults) -> transResults.forEach((transBody) -> {
                    transBody.setSrc(transBody.getSrc().replaceAll("[\\s]+", " ").trim());
                    transBody.setDst(transBody.getDst().replaceAll("[\\s]+", " ").trim());
                }));
        return body;
    }
}
