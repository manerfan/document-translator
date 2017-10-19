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

package com.manerfan.translator.document.encoding;

import com.manerfan.translator.TranslatorAutoConfiguration;
import com.manerfan.translator.api.baidu.BaiduTransAutoConfiguration;
import com.manerfan.translator.api.document.DocumentTransAutoConfiguration;
import com.manerfan.translator.api.sbd.SBDAutoConfiguration;
import com.manerfan.translator.jpa.JpaAutoConfiguration;
import info.monitorenter.cpdetector.io.CodepageDetectorProxy;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author manerfan
 * @date 2017/10/26
 */

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {
        JpaAutoConfiguration.class,
        TranslatorAutoConfiguration.class,
        SBDAutoConfiguration.class,
        BaiduTransAutoConfiguration.class,
        DocumentTransAutoConfiguration.class})
@EnableAutoConfiguration(exclude = JpaRepositoriesAutoConfiguration.class)
public class EncodeDetectTest {
    @Value("classpath:/encoding/GB2312.txt")
    Resource gb2312;

    @Value("classpath:/encoding/UTF8.txt")
    Resource utf8;

    @Autowired
    CodepageDetectorProxy codepageDetectorProxy;

    @Test
    public void encodeDetectTest() throws Exception {
        Assert.assertEquals("GB2312", codepageDetectorProxy.detectCodepage(gb2312.getURL()).name());
        Assert.assertEquals("UTF-8", codepageDetectorProxy.detectCodepage(utf8.getURL()).name());
    }
}
