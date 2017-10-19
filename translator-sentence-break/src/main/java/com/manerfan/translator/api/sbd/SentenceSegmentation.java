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

package com.manerfan.translator.api.sbd;

import com.google.common.base.CharMatcher;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.manerfan.translator.jpa.repositories.StatisticsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * @author manerfan
 * @date 2017/10/20
 */

@Component
public class SentenceSegmentation {
    @Value("${puncture.end:。！？.!?\r\n}")
    private String punctureEnd;

    @Value("${puncture.nblank:0123456789abcdefghijklmnopqrstuvwxyz_-.}")
    private String nBlank;

    private CharMatcher punctureEndMatcher, nBlankMatcher;

    @Autowired
    StatisticsRepository statisticsRepository;

    @PostConstruct
    void initMatcher() {
        punctureEndMatcher = CharMatcher.anyOf(punctureEnd);
        nBlankMatcher = CharMatcher.anyOf(nBlank);
    }

    public Sentences segment(String section) {
        statisticsRepository.increaseSbdNum();
        return new Sentences(section);
    }

    public class Sentences implements Iterator<String> {
        char[] chars;

        int length, head, tail;

        public Sentences(String section) {
            this.chars = Strings.nullToEmpty(section).trim().toCharArray();
            this.length = this.chars.length;
        }

        private boolean isAbbreviation() {
            if (tail + 1 >= length) {
                return false;
            }
            return nBlankMatcher.matches(chars[tail + 1]);
        }

        private int nextAbbreviation() {
            int index = tail + 1;
            for (; index < length; index++) {
                if (!nBlankMatcher.matches(chars[index])) {
                    return index;
                }
            }

            return index - 1;
        }

        private boolean isQuotPunctured() {
            String subStr = String.valueOf(chars, head, tail - head);
            // 发现左引号但是没有发现右引号，则标记为不匹配
            return !(subStr.contains("“") && !subStr.contains("”"));
        }

        private int nextQuotPunctured() {
            int index = tail;
            for (; index < length; index++) {
                if ('”' == chars[index]) {
                    return index + 1;
                }
            }

            return index;
        }

        @Override
        public boolean hasNext() {
            return head < length;
        }

        @Override
        public String next() {
            for (; tail < length; tail++) {
                if (punctureEndMatcher.matches(chars[tail])) {
                    // 找到了句末标点

                    if (chars[tail] == '.' && isAbbreviation()) {
                        // 缩略词
                        tail = nextAbbreviation();
                        continue;
                    }

                    // 找到了句末
                    tail++;
                    break;
                }
            }

            for (; tail < length; tail++) {
                if (!punctureEndMatcher.matches(chars[tail])) {
                    break;
                }
            }

            if (!isQuotPunctured()) {
                // 左右引号未匹配
                tail = nextQuotPunctured();
            }

            String sub = String.valueOf(chars, head, tail - head);
            head = tail;

            return sub.trim();
        }

        @Override
        public void forEachRemaining(Consumer<? super String> action) {
            Objects.requireNonNull(action);
            while (hasNext()) {
                action.accept(next());
            }
        }

        public List<String> toStringList() {
            List<String> strings = Lists.newLinkedList();
            forEachRemaining((sentence) -> strings.add(sentence));
            return strings;
        }

        public String toPlainText() {
            return Joiner.on("\n").join(toStringList());
        }
    }
}
