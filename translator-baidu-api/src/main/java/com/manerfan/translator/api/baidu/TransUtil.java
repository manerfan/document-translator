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

package com.manerfan.translator.api.baidu;

import com.manerfan.translator.api.baidu.body.BaiduResponseBody;
import com.manerfan.translator.api.baidu.filters.TransFilter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by manerfan on 2017/10/16.
 */

@Component
public class TransUtil extends BaiduTransUtil {
    List<TransFilter> filters = new LinkedList<>();

    public TransUtil addFilter(TransFilter transFilter) {
        filters.add(transFilter);
        return this;
    }

    Pattern chinesePattern = Pattern.compile("[\u4e00-\u9fa5]");

    /**
     * 单句翻译
     *
     * @param q    required，要翻译的文本
     * @param from optional，翻译源语言，可设置为auto
     * @param to   optional，译文语言，缺省或设置为auto则根据q判断，若q中包含中文则为en，否则为zh
     * @return 译文
     */
    public String trans(String q, String from, String to) throws Exception {
        for (TransFilter filter : filters) {
            q = filter.pre(q);
        }

        BaiduResponseBody responseBody = transReq(q, from, to);

        for (TransFilter filter : filters) {
            responseBody = filter.post(responseBody);
        }

        return responseBody.getDst();
    }
}
