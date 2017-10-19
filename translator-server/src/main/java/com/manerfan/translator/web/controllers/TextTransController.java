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
import com.manerfan.translator.api.baidu.TranslatorManager;
import com.manerfan.translator.web.controllers.body.ResponseBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * TextTransController
 *
 * @author manerfan
 * @date 2017/10/12
 */

@RestController
public class TextTransController extends ApiControllerBase {
    @Autowired
    TranslatorManager transUtil;

    @Autowired
    ObjectMapper objectMapper;

    @RequestMapping("/text")
    @PostMapping(consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseBody trans(@RequestParam String q,
                              @RequestParam(required = false, defaultValue = "auto") String from,
                              @RequestParam String to) throws Exception {
        return ResponseBody.newBody(transUtil.transText(q, from, to));
    }
}
