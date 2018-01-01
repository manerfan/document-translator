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

package com.manerfan.translator.api.baidu

import com.manerfan.translator.TranslatorAutoConfiguration
import com.manerfan.translator.api.sbd.SbdAutoConfiguration
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.test.context.junit4.SpringRunner

/**
 * @author manerfan
 * @date 2017/12/6
 */

@RunWith(SpringRunner::class)
@SpringBootTest(classes = [
    TranslatorAutoConfiguration::class,
    SbdAutoConfiguration::class,
    BaiduTransAutoConfiguration::class
])
class TranslatorManagerTest {
    @Autowired
    lateinit var translatorUtil: TranslatorManager

    @Test
    fun transTextTest() {
        var response = translatorUtil.transText("Hello World!", "en", "zh")
        println("transTextTest: $response")
    }

    @Test
    fun transTextArrayTest() {
        var responses = translatorUtil.transTextArray(listOf("Hello World!", "Hello You!"), "en", "zh")
        println("transTextArrayTest: $responses")
    }

    @Test
    fun transSectionTest() {
        var responses = translatorUtil.transSection("Hello You!", "en", "zh")
        println("transSectionTest: $responses")
    }

    @Test
    fun transSectionArrayTest() {
        var responses = translatorUtil.transSectionArray(listOf("Hello World!", "Hello You!"), "en", "zh")
        println("transSectionArrayTest: $responses")
    }
}