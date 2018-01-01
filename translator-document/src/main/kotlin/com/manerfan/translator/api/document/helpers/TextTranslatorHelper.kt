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

package com.manerfan.translator.api.document.helpers

import com.google.common.collect.Lists
import com.manerfan.translator.api.baidu.TranslatorManager
import info.monitorenter.cpdetector.io.CodepageDetectorProxy
import org.springframework.stereotype.Component
import java.io.*

/**
 * @author manerfan
 * @date 2017/12/24
 */

@Component
class TextTranslatorHelper(
        val codePageDetector: CodepageDetectorProxy,
        val translatorManager: TranslatorManager
) : TranslatorHelper() {
    override var extension: String = "txt"

    override fun translate(srcFile: File, dstFile: File, from: String, to: String) {
        var charset = codePageDetector.detectCodepage(srcFile.toURI().toURL())
        var sentences = Lists.newArrayListWithCapacity<String>(batchSize)

        BufferedWriter(OutputStreamWriter(FileOutputStream(dstFile), charset)).use {
            fun writeToFile(sentences: Collection<String>) {
                for (sentence in sentences) {
                    it.write(sentence)
                    it.newLine()
                }
                it.flush()
            }

            BufferedReader(InputStreamReader(FileInputStream(srcFile), charset)).useLines {
                it.forEach {
                    if (sentences.size >= batchSize) {
                        writeToFile(translatorManager.transSectionArray(sentences, from, to))
                        sentences = Lists.newArrayListWithCapacity<String>(batchSize)
                    }

                    sentences.add(it)
                }
                writeToFile(translatorManager.transSectionArray(sentences, from, to))
            }
        }
    }
}