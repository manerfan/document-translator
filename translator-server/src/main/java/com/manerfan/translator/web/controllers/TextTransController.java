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

import com.manerfan.translator.api.baidu.TransUtil;
import com.manerfan.translator.web.controllers.body.ResponseBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by manerfan on 2017/10/12.
 */

@RestController
@RequestMapping("/trans/text")
public class TextTransController {
    @Autowired
    TransUtil transUtil;

    @GetMapping
    public ResponseBody trans(@RequestParam String q,
                              @RequestParam(required = false, defaultValue = "auto") String from,
                              @RequestParam(required = false) String to,
                              @RequestParam(required = false) String id) throws Exception {

        String dst = transUtil.trans(q, from, to);
        Map<String, Object> data = new HashMap<>();
        data.put("src", q);
        data.put("dst", dst);

        if (StringUtils.hasText(id)) {
            data.put("id", id);
        }

        return new ResponseBody(200, "success", data);
    }
}
