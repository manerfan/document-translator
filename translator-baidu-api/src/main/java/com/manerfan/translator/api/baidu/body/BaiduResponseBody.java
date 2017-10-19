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

package com.manerfan.translator.api.baidu.body;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author manerfan
 * @date 2017/10/12
 */

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class BaiduResponseBody implements Serializable {
    /**
     * 错误码
     */
    String error_code;

    /**
     * 错误信息
     */
    String error_msg;

    /**
     * 翻译源语言
     */
    String from;

    /**
     * 译文语言
     */
    String to;

    /**
     * 翻译结果
     */
    List<BaiduTransBody> trans_result;

    public BaiduResponseBody(String from, String to, List<BaiduTransBody> trans_result) {
        this.from = from;
        this.to = to;
        this.trans_result = trans_result;
    }

    public BaiduResponseBody() {
    }

    public String getDst(String delimiter) {
        if (CollectionUtils.isEmpty(trans_result)) {
            return "";
        }

        return trans_result.stream()
                .map((transBody) -> transBody.dst)
                .collect(Collectors.joining(delimiter));
    }

    @JsonIgnore
    public String getDst() {
        return getDst("\n");
    }

    @JsonIgnore
    public List<String> getDsts() {
        if (CollectionUtils.isEmpty(trans_result)) {
            return Collections.EMPTY_LIST;
        }

        return trans_result.stream()
                .map((transBody) -> transBody.dst)
                .collect(Collectors.toList());
    }

    public String getError_code() {
        return error_code;
    }

    public void setError_code(String error_code) {
        this.error_code = error_code;
    }

    public String getError_msg() {
        return error_msg;
    }

    public void setError_msg(String error_msg) {
        this.error_msg = error_msg;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public List<BaiduTransBody> getTrans_result() {
        return trans_result;
    }

    public void setTrans_result(List<BaiduTransBody> trans_result) {
        this.trans_result = trans_result;
    }

    @Override
    public String toString() {
        return "ResponseBody{" +
                "error_code='" + error_code + '\'' +
                ", error_msg='" + error_msg + '\'' +
                '}';
    }
}
