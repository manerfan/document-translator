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

import java.io.File;
import java.util.Collection;
import java.util.List;

/**
 * @author manerfan
 * @date 2017/10/26
 */

public abstract class TranslatorHelper {
    int batchSize = 100;

    /**
     * 将srcFile按照from语言翻译为to语言，并存为dstFile
     *
     * @param srcFile 源文件
     * @param dstFile 译文件
     * @param from    源语言
     * @param to      译文语言
     *
     * @throws Exception
     */
    public abstract void translate(File srcFile, File dstFile, String from, String to) throws Exception;

    public void translate(String srcFile, String dstFile, String from, String to) throws Exception {
        translate(new File(srcFile), new File(dstFile), from, to);
    }

    <T> List<List<T>> splitLists(Collection<T> collection) {
        List<List<T>> lists = Lists.newLinkedList();

        List<T> list = Lists.newArrayListWithCapacity(batchSize);
        for (T c : collection) {
            if (list.size() > batchSize) {
                lists.add(list);
                list = Lists.newArrayListWithCapacity(batchSize);
            }

            list.add(c);
        }
        lists.add(list);

        return lists;
    }
}
