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
 *
 */

package com.manerfan.translator.api.document

import com.manerfan.translator.TranslatorAutoConfiguration
import com.manerfan.translator.api.baidu.BaiduTransAutoConfiguration
import com.manerfan.translator.api.document.helpers.TextTranslatorHelper
import com.manerfan.translator.api.sbd.SbdAutoConfiguration
import info.monitorenter.cpdetector.io.CodepageDetectorProxy
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.io.Resource
import org.springframework.test.context.junit4.SpringRunner

/**
 * @author manerfan
 * @date 2017/12/20
 */

@RunWith(SpringRunner::class)
@SpringBootTest(classes = [
    TranslatorAutoConfiguration::class,
    SbdAutoConfiguration::class,
    BaiduTransAutoConfiguration::class,
    DocumentTransAutoConfiguration::class
])
class DocumentTransManagerTest {
    @Value("classpath:/encoding/GB2312.txt")
    lateinit var gb2312: Resource

    @Value("classpath:/encoding/UTF8.txt")
    lateinit var utf8: Resource

    @Value("classpath:/office/word.docx")
    lateinit var word: Resource

    @Value("classpath:/office/excel.xlsx")
    lateinit var excel: Resource

    @Value("classpath:/office/ppt.pptx")
    lateinit var ppt: Resource

    @Value("classpath:/text.txt")
    lateinit var text: Resource

    @Autowired
    lateinit var codePageDetectorProxy: CodepageDetectorProxy

    @Autowired
    lateinit var documentTransManager: DocumentTransManager

    @Test
    fun encodeDetectTest() {
        Assert.assertEquals("GB2312", codePageDetectorProxy.detectCodepage(gb2312.url).name())
        Assert.assertEquals("UTF-8", codePageDetectorProxy.detectCodepage(utf8.url).name())
    }

    @Test
    fun wordTransTest() {
        documentTransManager.translate(word.url.path, "zh", "en")
    }

    @Test
    fun pptTransTest() {
        documentTransManager.translate(ppt.url.path, "zh", "en")
    }


    @Test
    fun excelTransTest() {
        documentTransManager.translate(excel.url.path, "zh", "en")
    }

    @Test
    fun textTransTest() {
        documentTransManager.translate(text.url.path, "zh", "en")
    }
}