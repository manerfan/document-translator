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

import com.manerfan.translator.api.baidu.filters.InitThreadLocalFilter;
import com.manerfan.translator.api.baidu.filters.RegexThreadLocalFilter;
import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Created by manerfan on 2017/10/16.
 */

@Configuration
@ComponentScan(basePackages = "com.manerfan.translator.api.baidu")
public class BaiduTransAutoConfiguration {
    @Bean
    OkHttpClient okHttpClient() {
        return new OkHttpClient();
    }

    @Bean
    TransUtil transUtil() {
        TransUtil transUtil = new TransUtil();
        transUtil.addFilter(new InitThreadLocalFilter());
        /* EMAIL */
        transUtil.addFilter(new RegexThreadLocalFilter("(mailto:\\/\\/)?\\w[-\\w.+]*@([A-Za-z0-9][-A-Za-z0-9]+\\.)+[A-Za-z]{2,14}"));
        /* URL */
        transUtil.addFilter(new RegexThreadLocalFilter("((https|http|ssh|ftp|rtsp|mms):\\/\\/)?[\\w\\-]+(\\.[\\w\\-]+)+([\\w\\-\\.,@?^=%&:;\\/~\\+#]*[\\w\\-\\@?^=%&\\/~\\+#])?"));
        /* EMOTION */
        transUtil.addFilter(new RegexThreadLocalFilter("[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]"));
        /* @MENTION */
        transUtil.addFilter(new RegexThreadLocalFilter("@[^\\s\\pP\\pS]{1,12}"));
        return transUtil;
    }
}
