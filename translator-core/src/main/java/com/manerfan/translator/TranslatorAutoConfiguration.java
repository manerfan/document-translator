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

package com.manerfan.translator;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import feign.Client;
import okhttp3.OkHttpClient;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.feign.FeignAutoConfiguration;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.concurrent.*;

/**
 * @author manerfan
 * @date 2017/10/24
 */

@EnableFeignClients
@EnableHystrix
@EnableAsync
@EnableScheduling
@Configuration
@AutoConfigureBefore(FeignAutoConfiguration.class)
public class TranslatorAutoConfiguration implements InitializingBean {
    int availableProcessors = Runtime.getRuntime().availableProcessors();

    @Bean
    @ConditionalOnMissingBean
    ExecutorService executorService() {
        return new ThreadPoolExecutor(
                availableProcessors * 16,
                availableProcessors * 32,
                5, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(1024),
                new ThreadFactoryBuilder().setNameFormat("translator-pool-%d").build());
    }

    @Bean
    @ConditionalOnMissingBean
    ScheduledExecutorService scheduledExecutorService() {
        return Executors.newScheduledThreadPool(availableProcessors * 16);
    }

    @Bean
    public Executor asyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(2);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("spring-executor-pool-");
        executor.initialize();
        return executor;
    }

    @Bean
    @ConditionalOnMissingBean
    ObjectMapper objectMapper() {
        ObjectMapper jacksonMapper = new ObjectMapper();
        /* 属性不匹配时 忽略错误 */
        jacksonMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        /* 序列化|解析 枚举 均使用名字 */
        jacksonMapper.configure(SerializationFeature.WRITE_ENUMS_USING_TO_STRING, true);
        jacksonMapper.configure(DeserializationFeature.READ_ENUMS_USING_TO_STRING, true);

        /* 时间 使用如下格式 */
        jacksonMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS"));
        jacksonMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true);

        /* 不序列化NULL元素 */
        jacksonMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);

        return jacksonMapper;
    }

    @Bean
    OkHttpClient okHttpClient() {
        return new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS)
                .followRedirects(true)
                .followSslRedirects(true)
                .retryOnConnectionFailure(true)
                .build();
    }

    @Bean
    @Primary
    public Client feignClient(OkHttpClient okHttpClient) {
        // 覆盖springboot配置，强制使用okhttp
        // 还可以通过配置 feign.okhttp.enabled=true 指定
        return new feign.okhttp.OkHttpClient(okHttpClient);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        FileUtils.forceMkdir(new File("./data/log"));
    }
}
