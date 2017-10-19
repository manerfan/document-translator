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

package com.manerfan.translator.api.document;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.manerfan.translator.api.document.helpers.TranslatorHelper;
import com.manerfan.translator.exceptions.BusinessException;
import com.manerfan.translator.exceptions.ErrorCode;
import com.manerfan.translator.jpa.repositories.StatisticsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.File;
import java.util.Map;
import java.util.concurrent.Semaphore;

/**
 * @author manerfan
 * @date 2017/10/26
 */

@Component
public class DocumentTransManager {
    @Autowired
    StatisticsRepository statisticsRepository;

    final Semaphore semaphore = new Semaphore(10);

    Map<String, TranslatorHelper> translatorHelperMap = Maps.newHashMap();

    public void regist(String extension, TranslatorHelper translatorHelper) {
        translatorHelperMap.put(extension.toUpperCase(), translatorHelper);
    }

    /**
     * 翻译文档
     *
     * @param fullPath 源文档路径
     * @param from     源语言
     * @param to       译文语言
     *
     * @return 翻译文档路径 [src_name]_[to].[src_extension]
     */
    public String translate(String fullPath, String from, String to) throws Exception {
        try {
            semaphore.acquire();
            return transInternal(fullPath, from, to);
        } finally {
            semaphore.release();
        }
    }

    private String transInternal(String fullPath, String from, String to) throws Exception {
        Preconditions.checkArgument(StringUtils.hasText(fullPath), "filePath should not be empty!");
        Preconditions.checkArgument(StringUtils.hasText(from), "from should not be empty!");
        Preconditions.checkArgument(StringUtils.hasText(to), "to should not be empty!");

        File srcFile = new File(fullPath);
        Preconditions.checkArgument(srcFile.exists() && srcFile.isFile(), "File Not Exists or Not a File!");

        String srcName = Files.getNameWithoutExtension(fullPath);
        String srcExtension = Files.getFileExtension(fullPath);
        Preconditions.checkArgument(StringUtils.hasText(srcName), "File has no name!");
        Preconditions.checkArgument(StringUtils.hasText(srcExtension), "File has no extension!");

        File dstFile = new File(srcFile.getParent(), srcName + "_" + to + "." + srcExtension);
        if (dstFile.exists()) {
            Preconditions.checkArgument(dstFile.delete(), "Delete File " + dstFile.getAbsolutePath() + " Failed!");
        }

        TranslatorHelper translatorHelper = translatorHelperMap.get(srcExtension.toUpperCase());
        if (null == translatorHelper) {
            throw new BusinessException(ErrorCode.PARAMS_INVALID, srcExtension + " File Not Supported!");
        }
        translatorHelper.translate(srcFile, dstFile, from, to);

        statisticsRepository.increaseDocNum();

        return dstFile.getAbsolutePath();
    }
}
