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
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.DocumentPart;
import org.docx4j.wml.ContentAccessor;
import org.docx4j.wml.Text;
import org.springframework.stereotype.Component;

import javax.xml.bind.JAXBElement;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author manerfan
 * @date 2017/10/26
 */

@Component
public class DocxTranslatorHelper extends OfficeTranslatorHelper {
    public DocxTranslatorHelper(DocumentTransManager documentTransManager) {
        documentTransManager.regist("DOCX", this);
    }

    @Override
    public void translate(File srcFile, File dstFile, String from, String to) throws Exception {
        WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(srcFile);
        translateDoc(wordMLPackage.getMainDocumentPart(), from, to);
        wordMLPackage.save(dstFile);
    }

    void translateDoc(DocumentPart documentPart, String from, String to) throws Exception {
        List<Text> texts = getAllElementFromObject(documentPart.getContents(), Text.class);
        splitLists(texts).parallelStream().forEach((textList) -> {
            try {
                transTexts(textList, from, to);
            } catch (Exception ex) {
                logger.error("Translate docx Error!", ex);
            }
        });
    }

    void transTexts(List<Text> texts, String from, String to) throws Exception {
        List<String> dsts = translatorManager.transSentences(
                texts.stream().map(Text::getValue).collect(Collectors.toList()), from, to);

        int[] idx = {0};
        String space = nonspacelanguages.contains(to) ? "" : " ";
        texts.stream().forEach((text) -> {
            text.setSpace("preserve");
            text.setValue(dsts.get(idx[0]++) + space);
        });
    }

    <T> List<T> getAllElementFromObject(Object obj, Class<T> toSearch) {
        List<T> result = new ArrayList<>();
        if (obj instanceof JAXBElement) {
            obj = ((JAXBElement<?>) obj).getValue();
        }
        if (obj.getClass().equals(toSearch)) {
            result.add(toSearch.cast(obj));
        } else if (obj instanceof ContentAccessor) {
            List<?> children = ((ContentAccessor) obj).getContent();
            for (Object child : children) {
                result.addAll(getAllElementFromObject(child, toSearch));
            }
        }
        return result;
    }
}
