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

package com.manerfan.translator.api.baidu.filters;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.manerfan.translator.api.baidu.body.BaiduResponseBody;
import com.manerfan.translator.api.baidu.body.BaiduTransBody;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author manerfan
 * @date 2017/10/16
 */

public class RegexFilter implements TransFilter<String> {
    final String uuid = UUID.randomUUID().toString();

    /**
     * 模式
     */
    Pattern pattern;

    /**
     * 前缀
     */
    String prefix;

    /**
     * 后缀
     */
    String suffix;

    public RegexFilter(String pattern, String prefix, String suffix) {
        this.pattern = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
        this.prefix = prefix;
        this.suffix = suffix;
    }

    public RegexFilter(String pattern) {
        this(pattern, "", "");
    }

    private Set<String> symbols(String q) {
        Set<String> groups = Sets.newHashSet();
        Matcher matcher = pattern.matcher(q);
        while (matcher.find()) {
            groups.add(matcher.group());
        }

        return groups;
    }

    private String symbolId() {
        // 00[\d]{6} 目前只发现这种格式可以不被百度误认为为匈牙利语等小语种
        String symbolId = String.valueOf(Math.abs(UUID.randomUUID().hashCode()) % 1000000);
        return Strings.padStart(symbolId, 8, '0');
    }

    private Map<String, String> group(String q) {
        Map<String, String> ids = Maps.newHashMap();
        symbols(q).forEach((symbol) -> ids.put(symbolId(), symbol));
        return ids;
    }

    /**
     * 按照正则匹配，将q中特殊字符替换为随机字符
     *
     * @return
     */
    @Override
    public String pre(String q, FilterContext context) {
        Map<String, String> groups = group(q);
        context.putAll(uuid, groups);

        Iterator<Map.Entry<String, String>> groupIter = groups.entrySet().iterator();
        while (groupIter.hasNext()) {
            Map.Entry<String, String> entry = groupIter.next();
            q = StringUtils.replace(q, entry.getValue(), entry.getKey());
        }

        return q;
    }

    private void restore(BaiduTransBody body, FilterContext<String> context) {
        context.get(uuid).ifPresent((groups) ->
                groups.entrySet().forEach((entry) -> {
                    String replaceRegex = "(?i)" + entry.getKey();
                    String replacement = prefix + entry.getValue() + suffix;
                    body.setDst(body.getDst().replaceAll(replaceRegex, replacement));
                    body.setSrc(body.getSrc().replaceAll(replaceRegex, replacement));
                })
        );
    }

    /**
     * 将随机特殊字符替换为源字符
     *
     * @return
     */
    @Override
    public BaiduResponseBody post(BaiduResponseBody resp, FilterContext context) {
        Optional.ofNullable(resp.getTrans_result())
                .filter((transResults) -> !CollectionUtils.isEmpty(transResults))
                .ifPresent((transResults) -> transResults.forEach((transBody) -> restore(transBody, context)));
        return resp;
    }
}
