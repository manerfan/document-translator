/*
 * ManerFan(http://manerfan.com). All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License")
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.manerfan.translator.api.sbd

import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner

/**
 * @author manerfan
 * @date 2017/12/5
 */

@RunWith(SpringRunner::class)
@SpringBootTest(classes = [SbdAutoConfiguration::class])
class SentenceSegmentationTest {
    @Autowired
    lateinit var ss: SentenceSegmentation

    @Test
    fun test() {
        println("===============")
        println(ss.segment("        这里是中文。 This is English. 还有🙄\n有个回车哦…… 37.6678 e.g. 等等吧！e.g.").toPlainText())
        println("===============")
        println(ss.segment("他说：“谢谢”").toPlainText())
        println("===============")
        println(ss.segment("他讲了个笑话\n说：“你知道吗？哈哈哈，笑死我了！”好好笑").toPlainText())
        println("===============")
        println(ss.segment("他说：“滚犊子！丫的！”你说：“谢谢”").toPlainText())
        println("===============")
        println(ss.segment("Developers are constantly challenged with choosing the most effective runtime, programming model and architecture for their application's requirements and team's skill-set. For example, some use cases are best handled by a technology stack based on synchronous blocking I/O architecture while others would be better served by an asynchronous, and non-blocking stack built on the reactive design principles described in the Reactive Streams Specification.").toPlainText())
        println("===============")
        println(ss.segment("@顾佳凤 @于春深 今天晚上开会").toPlainText())
        println("===============")
    }
}