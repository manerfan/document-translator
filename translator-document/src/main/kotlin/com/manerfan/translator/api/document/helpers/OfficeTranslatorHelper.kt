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

import com.manerfan.translator.api.baidu.TranslatorManager
import com.manerfan.translator.api.document.DocumentTransManager
import org.docx4j.dml.CTRegularTextRun
import org.docx4j.dml.CTTextParagraph
import org.docx4j.openpackaging.packages.PresentationMLPackage
import org.docx4j.openpackaging.packages.SpreadsheetMLPackage
import org.docx4j.openpackaging.packages.WordprocessingMLPackage
import org.docx4j.openpackaging.parts.PresentationML.MainPresentationPart
import org.docx4j.wml.ContentAccessor
import org.docx4j.wml.Text
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.xlsx4j.sml.CTRst
import org.xlsx4j.sml.CTXstringWhitespace
import java.io.File
import java.util.stream.IntStream
import javax.xml.bind.JAXBElement

/**
 * @author manerfan
 * @date 2017/12/19
 */

abstract class OfficeTranslatorHelper : TranslatorHelper() {
    val noSpaceLanguages = setOf("zh", "th", "jp", "kor", "cht")

    @Autowired
    lateinit var translatorManager: TranslatorManager
}

@Component
class DocxTranslatorHelper : OfficeTranslatorHelper() {
    override var extension = "docx"

    override fun translate(srcFile: File, dstFile: File, from: String, to: String) {
        var wordMLPackage = WordprocessingMLPackage.load(srcFile)
        splitLists(getTextsFromObject(wordMLPackage.mainDocumentPart.contents)).parallelStream().forEach { transTexts(it, from, to) }
        wordMLPackage.save(dstFile)
    }

    private fun transTexts(texts: List<Text>, from: String, to: String) {
        val space = if (noSpaceLanguages.contains(to)) "" else " "
        translatorManager.transSectionArray(texts.map(Text::getValue), from, to).forEachIndexed { index, dst ->
            texts[index].space = "preserve"
            texts[index].value = dst + space
        }
    }

    private fun getTextsFromObject(obj: Any): List<Text> {
        var value = if (obj is JAXBElement<*>) obj.value else obj
        return when (value) {
            is Text -> listOf(value)
            is ContentAccessor -> value.content.orEmpty().flatMap { getTextsFromObject(it) }
            else -> emptyList()
        }
    }
}

@Component
class XlsxTranslatorHelper : OfficeTranslatorHelper() {
    override var extension: String = "xlsx"

    override fun translate(srcFile: File, dstFile: File, from: String, to: String) {
        var spreadSheetMLPackage = SpreadsheetMLPackage.load(srcFile)
        var ctxs = spreadSheetMLPackage.workbookPart.sharedStrings.contents.si.map(CTRst::getT).filter { null != it }
        splitLists(ctxs).forEach { transCtxs(it, from, to) }
        spreadSheetMLPackage.save(dstFile)
    }

    fun transCtxs(ctxs: List<CTXstringWhitespace>, from: String, to: String) {
        val space = if (noSpaceLanguages.contains(to)) "" else " "
        translatorManager.transSectionArray(ctxs.map(CTXstringWhitespace::getValue), from, to).forEachIndexed { index, dst ->
            ctxs[index].space = "preserve"
            ctxs[index].value = dst + space
        }
    }
}

@Component
class PptxTranslatorHelper : OfficeTranslatorHelper() {
    override var extension: String = "pptx"

    override fun translate(srcFile: File, dstFile: File, from: String, to: String) {
        var pptMLPackage = PresentationMLPackage.load(srcFile)
        var documentPart = pptMLPackage.mainPresentationPart
        IntStream.range(0, documentPart.slideCount).parallel().forEach { translateSlide(documentPart, it, from, to) }
        pptMLPackage.save(dstFile)
    }

    fun translateSlide(presentationPart: MainPresentationPart, idx: Int, from: String, to: String) {
        var texts = presentationPart.getSlide(idx).getJAXBNodesViaXPath("//a:p", true)
                .filter { it is CTTextParagraph }.map { it as CTTextParagraph }.map(CTTextParagraph::getEGTextRun)
                .flatMap { it.filter { it is CTRegularTextRun }.map { it as CTRegularTextRun } }
        transTexts(texts, from, to)
    }

    fun transTexts(texts: List<CTRegularTextRun>, from: String, to: String) {
        val space = if (noSpaceLanguages.contains(to)) "" else " "
        translatorManager.transSectionArray(texts.map(CTRegularTextRun::getT), from, to).forEachIndexed { index, dst -> texts[index].t = dst + space }
    }
}