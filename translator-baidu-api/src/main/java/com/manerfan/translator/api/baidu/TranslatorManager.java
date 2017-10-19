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

package com.manerfan.translator.api.baidu;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.manerfan.translator.api.baidu.body.BaiduResponseBody;
import com.manerfan.translator.api.baidu.body.BaiduTransBody;
import com.manerfan.translator.api.baidu.filters.FilterContext;
import com.manerfan.translator.api.baidu.filters.TransFilter;
import com.manerfan.translator.api.sbd.SentenceSegmentation;
import com.manerfan.translator.jpa.repositories.StatisticsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author manerfan
 * @date 2017/10/16
 */

public class TranslatorManager extends BaiduTransUtil {
    Logger logger = LoggerFactory.getLogger(TranslatorManager.class);

    Pattern preBlankPattern = Pattern.compile("^[\\s]+");

    @Autowired
    SentenceSegmentation sentenceSegmentation;

    @Autowired
    StatisticsRepository statisticsRepository;

    ListeningExecutorService executorService;

    List<TransFilter> filters = new LinkedList<>();

    public TranslatorManager(ExecutorService executorService) {
        this.executorService = MoreExecutors.listeningDecorator(executorService);
    }

    public TranslatorManager addFilter(TransFilter transFilter) {
        filters.add(transFilter);
        return this;
    }

    /**
     * 短文本翻译
     *
     * @param q    required，要翻译的文本
     * @param from optional，翻译源语言，可设置为auto
     * @param to   optional，译文语言，缺省为zh
     *
     * @return 译文
     *
     * @throws Exception
     */
    public BaiduResponseBody transText(String q, String from, String to) throws Exception {
        Preconditions.checkArgument(StringUtils.hasText(q), "q should not be empty");

        FilterContext context = FilterContext.<String>newInstance();

        /* 预处理 */
        for (TransFilter filter : filters) {
            q = filter.pre(q, context);
        }

        BaiduResponseBody response = transReq(q, from, to);

        /* 后处理 */
        for (TransFilter filter : filters) {
            response = filter.post(response, context);
        }

        statisticsRepository.increaseTextNum();
        statisticsRepository.increaseBytesNum(q.getBytes().length);

        return response;
    }

    /**
     * 批量短文本翻译
     *
     * @param qs   required，要翻译的文本
     * @param from optional，翻译源语言
     * @param to   optional，译文语言
     *
     * @return 译文
     *
     * @throws Exception
     * @see TranslatorManager#transText(String, String, String)
     */
    private List<BaiduResponseBody> transTextArray(Collection<String> qs, String from, String to) throws Exception {
        Preconditions.checkArgument(!CollectionUtils.isEmpty(qs), "qs should not be empty");

        return Futures.allAsList(qs.stream().map((q) -> executorService.submit(
                () -> transText(q, from, to))
        ).collect(Collectors.toList())).get();
    }

    /**
     * 将长文本拆分为短文本
     *
     * @param sentence 长文本
     *
     * @return 短文本
     */
    private List<String> fixedToSentences(String sentence) {
        SentenceSegmentation.Sentences segment = sentenceSegmentation.segment(sentence);

        int l = 0;
        StringBuilder sb = new StringBuilder();
        List<String> sentences = Lists.newLinkedList();
        while (segment.hasNext()) {
            String s = segment.next();
            int len = s.getBytes().length;

            if (l + len > limit) {
                sentences.add(sb.toString());
                sb = new StringBuilder();
                l = 0;
            }

            l += len;
            sb.append(s);
        }
        sentences.add(sb.toString());

        return sentences;
    }

    /**
     * 长文本翻译（忽略回车符）
     *
     * @param s    required，要翻译的文本
     * @param from required，翻译源语言
     * @param to   required，译文语言
     *
     * @return 译文
     *
     * @throws Exception
     */
    BaiduResponseBody transSection(String s, String from, String to) throws Exception {
        Preconditions.checkArgument(StringUtils.hasText(s), "s should not be empty");
        Preconditions.checkArgument(StringUtils.hasText(from), "from should not be empty");
        Preconditions.checkArgument(StringUtils.hasText(to), "to should not be empty");

        List<String> sentences;
        if (s.getBytes().length > limit) {
            sentences = fixedToSentences(s);
        } else {
            sentences = Lists.newArrayList(s);
        }

        List<BaiduResponseBody> bodies = transTextArray(sentences, from, to);
        String dst = Joiner.on("").join(bodies.stream().map((body) -> body.getDst()).collect(Collectors.toList()));

        return new BaiduResponseBody(from, to, Lists.newArrayList(new BaiduTransBody(s, dst)));
    }

    /**
     * 批量翻译句子/段落<br/>
     * (忽略每一条子数据中的回车符)
     *
     * @param sentences required，要翻译的句子/段落
     * @param from      required，翻译源语言
     * @param to        required，译文语言
     *
     * @return 顺序相对应的翻译结果
     *
     * @throws Exception
     */
    public List<String> transSentences(List<String> sentences, String from, String to) throws Exception {
        if (CollectionUtils.isEmpty(sentences)) {
            return Lists.newArrayList();
        }

        List<SentenceWrapper> wrappers = wrapSentences(sentences);
        List<List<SentenceWrapper>> groupWarppers = groupWrapperList(wrappers);
        groupWarppers.parallelStream().forEach((groupWrapper) -> {
            try {
                if (groupWrapper.size() > 1) {
                    List<BaiduResponseBody> dsts = transTextArray(
                            groupWrapper.stream().map((wrapper) -> wrapper.src).collect(Collectors.toList()), from, to);

                    int[] idx = {0};
                    groupWrapper.stream().forEach((wrapper) -> wrapper.dst = dsts.get(idx[0]++).getDst());

                } else {
                    groupWrapper.get(0).dst = transSection(groupWrapper.get(0).src, from, to).getDst();
                }
            } catch (Exception e) {
                logger.error("TransSentence Error!", e);
            }
        });

        return wrappers.stream().map(SentenceWrapper::dstString).collect(Collectors.toList());
    }

    private List<List<SentenceWrapper>> groupWrapperList(List<SentenceWrapper> wrappers) {
        List<List<SentenceWrapper>> groupWrappers = Lists.newLinkedList();

        int l = 0;
        List<SentenceWrapper> groupWrapper = Lists.newLinkedList();
        Iterator<SentenceWrapper> wrapperIterator = wrappers.iterator();
        while (wrapperIterator.hasNext()) {
            SentenceWrapper wrapper = wrapperIterator.next();
            if (!wrapper.srcHasText()) {
                // 不含有效字符的略过
                continue;
            }

            int len = wrapper.srcBytesLen();

            if (l + len > limit) {
                groupWrappers.add(groupWrapper);
                groupWrapper = Lists.newLinkedList();
                l = 0;
            }

            l += len;
            groupWrapper.add(wrapper);
        }
        groupWrappers.add(groupWrapper);

        return groupWrappers;
    }

    private List<SentenceWrapper> wrapSentences(List<String> sentences) {
        return sentences.stream().map(SentenceWrapper::new).collect(Collectors.toList());
    }

    private class SentenceWrapper {
        String prefix = "";
        String src;
        String dst;

        public SentenceWrapper(String src) {
            Matcher matcher = preBlankPattern.matcher(src);
            if (matcher.find()) {
                prefix = matcher.group();
                src = matcher.replaceFirst("");
            }

            this.src = src;
            this.dst = this.src;
        }

        boolean srcHasText() {
            return StringUtils.hasText(src);
        }

        int srcBytesLen() {
            return src.getBytes().length;
        }

        public String dstString() {
            return prefix + dst;
        }
    }
}
