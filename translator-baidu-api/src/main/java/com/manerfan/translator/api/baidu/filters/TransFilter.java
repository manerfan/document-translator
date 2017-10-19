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


/**
 * @author manerfan
 * @date 2017/10/16
 */

public interface TransFilter<T> {
    /**
     * 预处理
     *
     * @param q             翻译原文
     * @param filterContext 上下文
     *
     * @return 预处理结果
     */
    String pre(String q, FilterContext<T> filterContext);

    /**
     * 后处理
     *
     * @param body          返回结果
     * @param filterContext 上下文
     *
     * @return 后处理结果
     */
    BaiduResponseBody post(BaiduResponseBody body, FilterContext<T> filterContext);
}
