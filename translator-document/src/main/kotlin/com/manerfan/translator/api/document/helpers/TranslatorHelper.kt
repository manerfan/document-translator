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

package com.manerfan.translator.api.document.helpers

import java.io.File

/**
 * @author manerfan
 * @date 2017/12/19
 */

abstract class TranslatorHelper {
    val batchSize = 100

    open lateinit var extension: String

    abstract fun translate(srcFile: File, dstFile: File, from: String, to: String)
    fun translate(srcFile: String, dstFile: String, from: String, to: String) {
        translate(File(srcFile), File(dstFile), from, to)
    }

    fun <T> splitLists(c: Collection<T>): List<List<T>> {
        var lists = mutableListOf<List<T>>()
        var list = mutableListOf<T>()

        c.forEach {
            if (list.size > batchSize) {
                lists.add(list)
                list = mutableListOf<T>()
            }
            list.add(it)
        }
        lists.add(list)

        return lists
    }
}