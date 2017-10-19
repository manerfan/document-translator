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

import com.google.common.collect.Maps;
import org.springframework.util.CollectionUtils;

import java.util.Map;
import java.util.Optional;

/**
 * @author manerfan
 * @date 2017/10/19
 */

public class FilterContext<T> {

    /**
     * 使用ThreadLocal，重构为RxJava时会有风险
     */

    Map<String, Map<String, T>> context = Maps.newHashMap();

    public static <T> FilterContext<T> newInstance() {
        return new FilterContext<>();
    }

    private FilterContext() {
    }

    void checkAndInit(String id) {
        if (!context.containsKey(id)) {
            context.put(id, Maps.newHashMap());
        }
    }

    public void putAll(String id, Map<String, T> map) {
        Optional.ofNullable(map).filter((m) -> !CollectionUtils.isEmpty(m)).ifPresent((m) -> {
            checkAndInit(id);
            context.get(id).putAll(map);
        });
    }

    public Optional<Map<String, T>> get(String id) {
        return Optional.ofNullable(context.get(id));
    }

}
