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

package com.manerfan.translator.api.baidu

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.AutoConfigureAfter
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import java.io.Serializable

/**
 * @author manerfan
 * @date 2017/12/5
 */

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
data class BaiduTransBody(var src: String, var dst: String) : Serializable

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
data class BaiduResponseBody(
        var from: String?, var to: String?,
        var trans_result: List<BaiduTransBody>?,
        var error_code: String? = null,
        var error_msg: String? = null
) : Serializable

interface RestService {
    @FormUrlEncoded
    @POST("api/trans/vip/translate")
    fun translate(
            @Field("q") q: String,
            @Field("from") from: String,
            @Field("to") to: String,
            @Field("appid") appId: String,
            @Field("salt") salt: Long,
            @Field("sign") sign: String
    ): Call<BaiduResponseBody>
}

@Configuration
class RestServiceConfigure {
    @Value("\${baidu.trans.baseUrl:https://fanyi-api.baidu.com}")
    private lateinit var url: String

    @Bean
    fun restService(okHttpClient: OkHttpClient, objectMapper: ObjectMapper): RestService {
        return Retrofit.Builder().baseUrl(url).client(okHttpClient)
                .addConverterFactory(JacksonConverterFactory.create(objectMapper))
                .build().create(RestService::class.java)
    }
}


