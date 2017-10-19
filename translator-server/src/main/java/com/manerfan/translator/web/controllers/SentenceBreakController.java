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

import com.manerfan.translator.api.sbd.SentenceSegmentation;
import com.manerfan.translator.web.controllers.body.ResponseBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author manerfan
 * @date 2017/10/23
 */

@RestController
public class SentenceBreakController extends ApiControllerBase {
    @Autowired
    SentenceSegmentation sentenceSegmentation;

    @PostMapping("/sbd")
    public ResponseBody sentenceBreak(@RequestParam String q) {
        return ResponseBody.newBody(sentenceSegmentation.segment(q).toStringList());
    }
}
