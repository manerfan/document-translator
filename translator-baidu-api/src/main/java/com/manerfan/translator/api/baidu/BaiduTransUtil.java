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

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.manerfan.translator.api.baidu.body.BaiduResponseBody;
import com.manerfan.translator.api.baidu.crypto.MD5;
import com.manerfan.translator.api.baidu.restservice.BaiduRestService;
import com.manerfan.translator.exceptions.BusinessException;
import com.manerfan.translator.exceptions.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;

import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.Optional;

/**
 * @author manerfan
 * @date 2017/10/12
 */

public class BaiduTransUtil {
    Logger logger = LoggerFactory.getLogger(BaiduTransUtil.class);

    @Value("${baidu.trans.app.id}")
    String appId;

    @Value("${baidu.trans.app.secret}")
    String appSecret;

    @Value("${baidu.trans.limit:5000}")
    int limit;

    @Autowired
    BaiduRestService baiduRestService;

    @Autowired
    MD5 md5;

    /**
     * 生成签名
     *
     * @param q    要翻译的文本
     * @param salt 盐
     *
     * @return
     *
     * @throws NoSuchAlgorithmException
     */
    private String sign(String q, String salt) throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append(appId).append(q).append(salt).append(appSecret);
        String sign = md5.encrypt(sb.toString());
        return String.format("%32s", sign).replaceAll("\\s", "0");
    }

    /**
     * 请求百度翻译API
     *
     * @param q    要翻译的文本
     * @param from 翻译源语言
     * @param to   译文语言
     *
     * @return 翻译结果
     *
     * @throws Exception
     */
    private BaiduResponseBody postFormDataForObject(String q, String from, String to) throws Exception {
        String salt = String.valueOf(System.currentTimeMillis());
        String sign = sign(q, salt);

        Map<String, Object> params = Maps.newHashMap();
        params.put("appid", appId);
        params.put("q", q);
        params.put("from", from);
        params.put("to", to);
        params.put("salt", salt);
        params.put("sign", sign);
        return baiduRestService.translate(params);
    }

    /**
     * 调用百度API进行翻译
     *
     * @param q    required，要翻译的文本
     * @param from optional，翻译源语言，可设置为auto
     * @param to   required，译文语言，不可设置为auto
     *
     * @return 译文
     *
     * @throws NoSuchAlgorithmException
     * @throws BusinessException
     */
    BaiduResponseBody transReq(String q, String from, String to) throws Exception {
        Preconditions.checkArgument(StringUtils.hasText(q), "q should not be empty");
        int len = q.getBytes().length;
        Preconditions.checkArgument(len <= limit, "q is too long (" + len + ") than " + limit + " bytes");
        Preconditions.checkArgument(StringUtils.hasText(to), "to should not be empty");

        if (!StringUtils.hasText(from)) {
            from = "auto";
        }

        BaiduResponseBody response = postFormDataForObject(q, from, to);

        if (StringUtils.hasText(response.getError_code())) {
            logger.warn("Baidu Transfor Error {}", response);
            throw new BusinessException(ErrorCode.REST_SERVICE_ERROR, Optional.of(response));
        }

        return response;
    }
}
