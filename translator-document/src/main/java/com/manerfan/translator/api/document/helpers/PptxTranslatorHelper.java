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

import com.manerfan.translator.api.document.DocumentTransManager;
import org.docx4j.dml.CTRegularTextRun;
import org.docx4j.dml.CTTextParagraph;
import org.docx4j.openpackaging.packages.PresentationMLPackage;
import org.docx4j.openpackaging.parts.PresentationML.MainPresentationPart;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author manerfan
 * @date 2017/10/26
 */

@Component
public class PptxTranslatorHelper extends OfficeTranslatorHelper {
    public PptxTranslatorHelper(DocumentTransManager documentTransManager) {
        documentTransManager.regist("PPTX", this);
    }

    @Override
    public void translate(File srcFile, File dstFile, String from, String to) throws Exception {
        PresentationMLPackage pptMLPackage = PresentationMLPackage.load(srcFile);
        MainPresentationPart documentPart = pptMLPackage.getMainPresentationPart();

        IntStream.range(0, documentPart.getSlideCount()).parallel().forEach((idx) -> translateSlide(documentPart, idx, from, to));
        pptMLPackage.save(dstFile);
    }

    void translateSlide(MainPresentationPart presentationPart, int idx, String from, String to) {
        try {
            List<CTRegularTextRun> texts = presentationPart.getSlide(idx).getJAXBNodesViaXPath("//a:p", true).stream()
                    .filter(CTTextParagraph.class::isInstance).map(CTTextParagraph.class::cast).map(CTTextParagraph::getEGTextRun)
                    .flatMap((runs) -> runs.stream().filter(CTRegularTextRun.class::isInstance).map(CTRegularTextRun.class::cast))
                    .collect(Collectors.toList());

            transTexts(texts, from, to);
        } catch (Exception ex) {
            logger.error("Trans PPTX Error!", ex);
        }
    }

    void transTexts(List<CTRegularTextRun> texts, String from, String to) throws Exception {
        List<String> dsts = translatorManager.transSentences(
                texts.stream().map(CTRegularTextRun::getT).collect(Collectors.toList()), from, to);

        int[] idx = {0};
        String space = nonspacelanguages.contains(to) ? "" : " ";
        texts.stream().forEach((text) -> text.setT(dsts.get(idx[0]++) + space));
    }
}
