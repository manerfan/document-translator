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

package com.manerfan.translator.api.document.helpers;

import com.google.common.collect.Lists;
import com.manerfan.translator.api.baidu.TranslatorManager;
import com.manerfan.translator.api.document.DocumentTransManager;
import info.monitorenter.cpdetector.io.CodepageDetectorProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.util.List;

/**
 * @author manerfan
 * @date 2017/10/26
 */

@Component
public class TextTranslatorHelper extends TranslatorHelper {

    @Autowired
    CodepageDetectorProxy codepageDetector;

    @Autowired
    TranslatorManager translatorManager;

    public TextTranslatorHelper(DocumentTransManager documentTransManager) {
        documentTransManager.regist("TXT", this);
    }

    @Override
    public void translate(File srcFile, File dstFile, String from, String to) throws Exception {
        Charset charset = codepageDetector.detectCodepage(srcFile.toURI().toURL());
        try (
                BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(srcFile), charset));
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dstFile), charset))
        ) {
            String line;
            List<String> sentences = Lists.newArrayListWithCapacity(batchSize);
            while (null != (line = br.readLine())) {
                if (sentences.size() > batchSize) {
                    writeToFile(translatorManager.transSentences(sentences, from, to), bw);
                    sentences = Lists.newArrayListWithCapacity(batchSize);
                }

                sentences.add(line);
            }
            writeToFile(translatorManager.transSentences(sentences, from, to), bw);
        }
    }

    private void writeToFile(List<String> sentences, BufferedWriter bw) throws IOException {
        if (CollectionUtils.isEmpty(sentences)) {
            return;
        }

        for (String sentence : sentences) {
            bw.write(sentence);
            bw.newLine();
        }

        bw.flush();
    }
}
