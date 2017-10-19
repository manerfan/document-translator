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
import org.docx4j.openpackaging.packages.SpreadsheetMLPackage;
import org.docx4j.openpackaging.parts.PresentationML.MainPresentationPart;
import org.docx4j.openpackaging.parts.SpreadsheetML.WorkbookPart;
import org.docx4j.openpackaging.parts.WordprocessingML.DocumentPart;
import org.docx4j.wml.ContentAccessor;
import org.docx4j.wml.Text;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.xlsx4j.sml.CTRst;
import org.xlsx4j.sml.CTSst;
import org.xlsx4j.sml.CTXstringWhitespace;

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
public class XlsxTranslatorHelper extends OfficeTranslatorHelper {
    public XlsxTranslatorHelper(DocumentTransManager documentTransManager) {
        documentTransManager.regist("XLSX", this);
    }

    @Override
    public void translate(File srcFile, File dstFile, String from, String to) throws Exception {
        SpreadsheetMLPackage spreadsheetMLPackage = SpreadsheetMLPackage.load(srcFile);
        WorkbookPart workbookPart = spreadsheetMLPackage.getWorkbookPart();
        List<CTXstringWhitespace> ctxs = workbookPart.getSharedStrings().getContents().getSi().stream()
                .map(CTRst::getT).filter((t) -> !ObjectUtils.isEmpty(t)).collect(Collectors.toList());

        transCtxs(ctxs, from, to);

        spreadsheetMLPackage.save(dstFile);
    }

    void transCtxs(List<CTXstringWhitespace> ctxs, String from, String to) throws Exception {
        List<String> dsts = translatorManager.transSentences(
                ctxs.stream().map(CTXstringWhitespace::getValue).collect(Collectors.toList()), from, to);

        int[] idx = {0};
        String space = nonspacelanguages.contains(to) ? "" : " ";
        ctxs.stream().forEach((text) -> {
            text.setSpace("preserve");
            text.setValue(dsts.get(idx[0]++) + space);
        });
    }
}
