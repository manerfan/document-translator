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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.manerfan.translator.api.baidu.body.BaiduResponseBody;
import com.manerfan.translator.api.baidu.crypto.MD5;
import com.manerfan.translator.exceptions.BusinessException;
import com.manerfan.translator.exceptions.ErrorCode;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Optional;

/**
 * Created by manerfan on 2017/10/12.
 */

public class BaiduTransUtil {
    Logger logger = LoggerFactory.getLogger(BaiduTransUtil.class);

    @Value("${baidu.trans.url:https://fanyi-api.baidu.com/api/trans/vip/translate}")
    String url;

    @Value("${baidu.trans.app.id}")
    String appId;

    @Value("${baidu.trans.app.secret}")
    String appSecret;

    @Autowired
    OkHttpClient okHttpClient;

    @Autowired
    MD5 md5;

    private ObjectMapper jacksonMapper = new ObjectMapper();

    @PostConstruct
    void initJacksonMapper() throws Exception {
        /* 属性不匹配时 忽略错误 */
        jacksonMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        /* 序列化|解析 枚举 均使用名字 */
        jacksonMapper.configure(SerializationFeature.WRITE_ENUMS_USING_TO_STRING, true);
        jacksonMapper.configure(DeserializationFeature.READ_ENUMS_USING_TO_STRING, true);

        /* 时间 使用如下格式 */
        jacksonMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS"));
        jacksonMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true);

        /* 不序列化NULL元素 */
        jacksonMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL.NON_NULL);
        /* 不序列化EMPTY元素 */
        jacksonMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
    }

    /**
     * 生成签名
     *
     * @param q    要翻译的文本
     * @param salt 盐
     * @return
     * @throws NoSuchAlgorithmException
     */
    private String sign(String q, String salt) throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append(appId).append(q).append(salt).append(appSecret);
        String sign = md5.encrypt(sb.toString());
        return String.format("%32s", sign).replaceAll("\\s", "0"); // 32位补齐
    }

    /**
     * 请求百度翻译API
     *
     * @param q    要翻译的文本
     * @param from 翻译源语言
     * @param to   译文语言
     * @return 翻译结果
     * @throws Exception
     */
    private BaiduResponseBody postFormDataForObject(String q, String from, String to) throws Exception {
        String salt = String.valueOf(System.currentTimeMillis());
        String sign = sign(q, salt);

        RequestBody formBody = new FormBody.Builder()
                .add("appid", appId)
                .add("q", q)
                .add("from", from)
                .add("to", to)
                .add("salt", salt)
                .add("sign", sign)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();

        Response response = okHttpClient.newCall(request).execute();
        String body = response.body().string();
        if (200 != response.code()) {
            logger.warn("Request Error for StatusCode {}, and Body {}", response.code(), body);
            throw new BusinessException(ErrorCode.REST_SERVICE_ERROR, Optional.of(body));
        }

        return jacksonMapper.readValue(body, BaiduResponseBody.class);
    }

    /**
     * 调用百度API进行翻译
     *
     * @param q    required，要翻译的文本
     * @param from optional，翻译源语言，可设置为auto
     * @param to   required，译文语言，不可设置为auto
     * @return 译文
     * @throws NoSuchAlgorithmException
     * @throws BusinessException
     */
    BaiduResponseBody transReq(String q, String from, String to) throws Exception {
        if (!StringUtils.hasLength(q)) {
            q = "";
        }

        if (!StringUtils.hasText(from)) {
            from = "auto";
        }

        if (!StringUtils.hasText(to)) {
            to = "zh";
        }

        BaiduResponseBody response = postFormDataForObject(q, from, to);
        if (StringUtils.hasText(response.getError_code())) {
            logger.warn("Baidu Transfor Error {}", response);
            throw new BusinessException(ErrorCode.REST_SERVICE_ERROR, Optional.of(response));
        }

        return response;
    }
}
