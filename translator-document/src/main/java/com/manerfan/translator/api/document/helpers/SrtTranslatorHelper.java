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

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.manerfan.translator.api.baidu.TranslatorManager;
import com.manerfan.translator.api.document.DocumentTransManager;
import info.monitorenter.cpdetector.io.CodepageDetectorProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author manerfan
 * @date 2017/10/26
 */

@Component
public class SrtTranslatorHelper extends TranslatorHelper {
    Logger logger = LoggerFactory.getLogger(SrtTranslatorHelper.class);

    @Autowired
    CodepageDetectorProxy codepageDetector;

    @Autowired
    TranslatorManager translatorManager;

    public SrtTranslatorHelper(DocumentTransManager documentTransManager) {
        documentTransManager.regist("SRT", this);
    }

    @Override
    public void translate(File srcFile, File dstFile, String from, String to) throws Exception {
        Charset charset = codepageDetector.detectCodepage(srcFile.toURI().toURL());
        try (
                BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(srcFile), charset));
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dstFile), charset))
        ) {
            List<SRTWrapper> wrappers = readAllWrappers(br);
            translateSrt(wrappers, from, to);
            writeToFile(wrappers, bw);
        }
    }

    private void writeToFile(List<SRTWrapper> wrappers, BufferedWriter bw) throws IOException {
        if (CollectionUtils.isEmpty(wrappers)) {
            return;
        }

        for (SRTWrapper wrapper : wrappers) {
            bw.write(wrapper.toString());
            bw.newLine();
        }

        bw.flush();
    }

    void translateSrt(List<SRTWrapper> wrappers, String from, String to) throws Exception {
        splitLists(wrappers).parallelStream().forEach((wrapperList) -> doTranslate(wrapperList, from, to));
    }

    void doTranslate(List<SRTWrapper> wrappers, String from, String to) {
        try {
            List<String> dsts = translatorManager.transSentences(
                    wrappers.stream().map(SRTWrapper::getUtterances).collect(Collectors.toList()), from, to);

            int[] idx = {0};
            wrappers.stream().forEach((wrapper) -> wrapper.dst = dsts.get(idx[0]++));
        } catch (Exception ex) {
            logger.error("Translate SRT Failed!", ex);
        }
    }

    private List<SRTWrapper> readAllWrappers(BufferedReader br) throws IOException {
        String line;
        SRTWrapper srtWrapper = null;
        List<SRTWrapper> srtWrappers = Lists.newLinkedList();

        int state = 0;
        while (null != (line = br.readLine())) {
            line = line.trim();

            switch (state) {
                case 0:
                    int no = Integer.valueOf(line);
                    srtWrapper = new SRTWrapper(no);
                    state++;
                    break;
                case 1:
                    srtWrapper.time = line;
                    state++;
                    break;
                case 2:
                    if (StringUtils.hasText(line)) {
                        srtWrapper.utterances.add(line);
                    } else {
                        state = 0;
                        srtWrappers.add(srtWrapper);
                    }
                    break;
                default:
                    break;
            }
        }

        return srtWrappers;
    }

    private class SRTWrapper {
        int no;
        String time;
        List<String> utterances = Lists.newLinkedList();
        String dst;

        public SRTWrapper(int no) {
            this.no = no;
        }

        public String getUtterances() {
            return Joiner.on("\n").join(utterances);
        }

        @Override
        public String toString() {
            return new StringBuilder()
                    .append(no).append('\n')
                    .append(time).append('\n')
                    .append(dst).append('\n')
                    .toString();
        }
    }
}
