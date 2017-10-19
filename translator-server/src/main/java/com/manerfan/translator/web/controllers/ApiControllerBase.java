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

package com.manerfan.translator.web.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author manerfan
 * @date 2017/10/22
 */

@RestController
@RequestMapping("/api/trans")
public abstract class ApiControllerBase {
    @Autowired
    ObjectMapper objectMapper;

    ObjectNode objectNode() {
        return objectMapper.createObjectNode();
    }

    Map<String, Object> objectMap() {
        return Maps.newHashMap();
    }

    Map<String, Object> objectMap(String key, Object value) {
        Map<String, Object> data = objectMap();
        data.put(key, value);
        return data;
    }
}
