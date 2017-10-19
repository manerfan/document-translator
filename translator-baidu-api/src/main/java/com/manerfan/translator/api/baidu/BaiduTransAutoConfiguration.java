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

import com.manerfan.translator.api.baidu.filters.RegexFilter;
import com.manerfan.translator.api.baidu.filters.TrimFilter;
import com.manerfan.translator.jpa.entities.StatisticsEntity;
import com.manerfan.translator.jpa.repositories.StatisticsRepository;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;
import java.util.concurrent.ExecutorService;

/**
 * @author manerfan
 * @date 2017/10/16
 */

@Configuration
@ComponentScan(basePackages = "com.manerfan.translator.api.baidu")
public class BaiduTransAutoConfiguration implements InitializingBean {

    @Autowired
    StatisticsRepository statisticsRepository;

    @Bean
    TranslatorManager transUtil(ExecutorService executorService) {
        TranslatorManager translatorUtil = new TranslatorManager(executorService);
        /* EMAIL */
        translatorUtil.addFilter(new RegexFilter("(mailto:\\/\\/)?\\w[-\\w.+]*@([A-Za-z0-9][-A-Za-z0-9]+\\.)+[A-Za-z]{2,14}"));
        /* URL */
        translatorUtil.addFilter(new RegexFilter("((https|http|ssh|ftp|rtsp|mms):\\/\\/)?[\\w\\-]+(\\.[\\w\\-]+)+([\\w\\-\\.,@?^=%&:;\\/~\\+#]*[\\w\\-\\@?^=%&\\/~\\+#])?"));
        /* EMOTION */
        translatorUtil.addFilter(new RegexFilter("[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]"));
        /* @MENTION */
        translatorUtil.addFilter(new RegexFilter("@[^\\s\\pP\\pS]{1,12}", "", " "));
        /* TRIM */
        translatorUtil.addFilter(new TrimFilter());

        return translatorUtil;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Optional<StatisticsEntity> statisticsEntity = statisticsRepository.getStatistics();
        statisticsEntity.orElseGet(() -> statisticsRepository.save(new StatisticsEntity()));
    }
}
