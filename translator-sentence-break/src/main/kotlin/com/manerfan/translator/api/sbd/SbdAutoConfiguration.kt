/*
 * ManerFan(http://manerfan.com). All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
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

import com.google.common.base.CharMatcher
import com.google.common.collect.Lists
import com.manerfan.translator.data.StatisticsRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.stereotype.Component
import java.util.function.Consumer
import javax.annotation.PostConstruct


/**
 * @author manerfan
 * @date 2017/12/5
 */

@Configuration
@Import(SentenceSegmentation::class)
class SbdAutoConfiguration

@Component
class SentenceSegmentation {
    @Value("\${puncture.end:…。！？.!?\r\n}")
    private lateinit var punctureEnd: String

    @Value("\${puncture.nblank:0123456789abcdefghijklmnopqrstuvwxyz_-.}")
    private lateinit var notBlank: String

    @Autowired
    lateinit var statisticsRepository: StatisticsRepository

    private lateinit var punctureEndMatcher: CharMatcher
    private lateinit var notBlankMatcher: CharMatcher

    @PostConstruct
    fun iniMatcher() {
        punctureEndMatcher = CharMatcher.anyOf(punctureEnd)
        notBlankMatcher = CharMatcher.anyOf(notBlank)
    }

    fun segment(section: String) = Sentences(section)

    inner class Sentences(section: String) : Iterator<String> {
        private var section = section.trim()
        private var length = this.section.length
        private var head = 0
        private var tail = 0

        init {
            statisticsRepository.increaseSbdTimes()
        }

        /**
         * 判断是否应该匹配缩略词
         */
        private fun isAbbreviation(): Boolean {
            return if (tail + 1 >= length) false else notBlankMatcher.matches(section[tail + 1])
        }

        /**
         * 查找缩略词截止index
         */
        private fun nextAbbreviation(): Int {
            var index = tail + 1
            while (index < length) {
                if (!notBlankMatcher.matches(section[index])) {
                    return index
                }
                index++
            }

            return index
        }

        /**
         * 判断是否应该匹配引号
         */
        private fun isQuotPunctured(): Boolean {
            val subStr = section.substring(head, tail)
            // 发现左引号但是没有发现右引号，则标记为不匹配
            return !(subStr.contains("“") && !subStr.contains("”"))
        }

        /**
         * 查找引号截止index
         */
        private fun nextQuotPunctured(): Int {
            var index = tail
            while (index < length) {
                if ('”' == section[index]) {
                    return index + 1
                }
                index++
            }

            return index
        }

        override fun hasNext(): Boolean {
            return head < length
        }

        override fun next(): String {
            while (tail < length) {
                if (punctureEndMatcher.matches(section[tail])) {
                    // 找到了句末标点

                    if (section[tail] == '.' && isAbbreviation()) {
                        // 缩略词
                        tail = nextAbbreviation()
                        continue
                    }

                    // 找到了句末
                    tail++
                    break
                }
                tail++
            }

            while (tail < length) {
                if (!punctureEndMatcher.matches(section[tail])) {
                    break
                }
                tail++
            }

            if (!isQuotPunctured()) {
                // 左右引号未匹配
                tail = nextQuotPunctured()
            }

            val sub = section.substring(head, tail)
            head = tail

            return sub.trim()
        }

        override fun forEachRemaining(action: Consumer<in String>) {
            while (hasNext()) {
                action.accept(next())
            }
        }

        fun toStringList(): List<String> {
            var strings = Lists.newLinkedList<String>()
            forEachRemaining { strings.add(it) }
            return strings
        }

        fun toPlainText(): String {
            return toStringList().joinToString("\n")
        }
    }
}