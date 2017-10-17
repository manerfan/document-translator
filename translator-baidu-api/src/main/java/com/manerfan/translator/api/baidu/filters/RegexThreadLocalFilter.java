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

import com.manerfan.translator.api.baidu.body.BaiduResponseBody;
import com.manerfan.translator.api.baidu.body.BaiduTransBody;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by manerfan on 2017/10/16.
 */

public class RegexThreadLocalFilter implements ThreadLocalFilter {
    final String uuid = UUID.randomUUID().toString();

    Pattern pattern;

    public RegexThreadLocalFilter(String pattern) {
        this.pattern = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
    }

    private Set<String> simbols(String q) {
        Set<String> groups = new HashSet<>();
        Matcher matcher = pattern.matcher(q);
        while (matcher.find()) {
            groups.add(matcher.group());
        }

        return groups;
    }

    private String simbolId() {
        String simbolId = Integer.toHexString(UUID.randomUUID().hashCode());
        return String.format("%8s", simbolId).replaceAll("[\\s]+", "0");
    }

    private Map<String, String> group(String q) {
        Map<String, String> ids = new HashMap<>();
        Set<String> simbols = simbols(q);
        for (String simbol : simbols) {
            ids.put(simbolId(), simbol);
        }

        return ids;
    }

    /**
     * 按照正则匹配，将q中特殊字符替换为随机字符
     *
     * @param q
     * @return
     */
    @Override
    public String pre(String q) {
        Map<String, String> groups = group(q);
        regexMap.get().put(uuid, groups);

        Iterator<Map.Entry<String, String>> groupIter = groups.entrySet().iterator();
        while (groupIter.hasNext()) {
            Map.Entry<String, String> entry = groupIter.next();
            q = StringUtils.replace(q, entry.getValue(), entry.getKey());
        }

        return q;
    }

    private void restore(BaiduTransBody body) {
        Map<String, String> groups = regexMap.get().get(uuid);

        Iterator<Map.Entry<String, String>> groupIter = groups.entrySet().iterator();
        while (groupIter.hasNext()) {
            Map.Entry<String, String> entry = groupIter.next();
            String replaceRegex = entry.getKey();
            body.setDst(body.getDst().replaceAll(replaceRegex, entry.getValue()));
            body.setSrc(body.getSrc().replaceAll(replaceRegex, entry.getValue()));
        }
    }

    /**
     * 将随机特殊字符替换为源字符
     *
     * @param resp
     * @return
     */
    @Override
    public BaiduResponseBody post(BaiduResponseBody resp) {
        List<BaiduTransBody> transResults = resp.getTrans_result();
        if (!CollectionUtils.isEmpty(transResults)) {
            transResults.stream().forEach((transBody) -> restore(transBody));
        }

        resp.setTrans_result(transResults);
        return resp;
    }
}
