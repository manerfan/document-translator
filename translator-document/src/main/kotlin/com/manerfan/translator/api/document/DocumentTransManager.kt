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

import com.google.common.base.Preconditions
import com.google.common.io.Files
import com.manerfan.translator.BusinessException
import com.manerfan.translator.ErrorCode
import com.manerfan.translator.api.document.helpers.TranslatorHelper
import com.manerfan.translator.data.StatisticsRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.io.File
import java.util.concurrent.Semaphore


/**
 * @author manerfan
 * @date 2017/12/19
 */

@Component
class DocumentTransManager(helpers: List<TranslatorHelper>) {
    @Autowired
    lateinit var statisticsRepository: StatisticsRepository

    var semaphore = Semaphore(50)

    var translatorHelperMap = mutableMapOf<String, TranslatorHelper>()

    init {
        helpers.forEach { translatorHelperMap[it.extension] = it }
    }

    fun translate(fullPath: String, from: String, to: String): String {
        try {
            semaphore.acquire()
            return transInternal(fullPath, from, to)
        } finally {
            semaphore.release()
        }
    }

    private fun transInternal(fullPath: String, from: String, to: String): String {
        val srcFile = File(fullPath)
        Preconditions.checkArgument(srcFile.exists() && srcFile.isFile, "File Not Exists or Not a File!")

        val srcName = Files.getNameWithoutExtension(fullPath)
        val srcExtension = Files.getFileExtension(fullPath)
        Preconditions.checkArgument(srcName.isNotBlank(), "File has no name!")
        Preconditions.checkArgument(srcExtension.isNotBlank(), "File has no extension!")

        val dstFile = File(srcFile.parent, srcName + "_" + to + "." + srcExtension)
        if (dstFile.exists()) {
            Preconditions.checkArgument(dstFile.delete(), "Delete File " + dstFile.absolutePath + " Failed!")
        }

        val translatorHelper = translatorHelperMap[srcExtension.toLowerCase()] ?: throw BusinessException(ErrorCode.PARAMS_INVALID, srcExtension + " File Not Supported!")
        translatorHelper.translate(srcFile, dstFile, from, to)

        statisticsRepository.increaseDocTimes()

        return dstFile.absolutePath

    }
}