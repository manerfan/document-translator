/*
 * ManerFan(http://manerfan.com). All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.manerfan.translator

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.google.common.util.concurrent.ThreadFactoryBuilder
import okhttp3.OkHttpClient
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import java.util.concurrent.*


/**
 * @author manerfan
 * @date 2017/12/3
 */

@Configuration
class TranslatorAutoConfiguration {
    private val availableProcessors = Runtime.getRuntime().availableProcessors()

    @Bean
    @ConditionalOnMissingBean
    fun executorService(): ExecutorService {
        return ThreadPoolExecutor(
                availableProcessors * 16,
                availableProcessors * 32,
                5, TimeUnit.SECONDS,
                ArrayBlockingQueue(1024),
                ThreadFactoryBuilder().setNameFormat("translator-pool-%d").build()
        )
    }

    @Bean
    @ConditionalOnMissingBean
    fun scheduledExecutorService(): ScheduledExecutorService {
        return Executors.newScheduledThreadPool(availableProcessors * 16)
    }

    @Bean
    fun asyncExecutor(): Executor {
        val executor = ThreadPoolTaskExecutor()
        executor.corePoolSize = 2
        executor.maxPoolSize = 2
        executor.setQueueCapacity(500)
        executor.threadNamePrefix = "spring-executor-pool-"
        executor.initialize()
        return executor
    }

    @Bean
    fun okHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS)
                .followRedirects(true)
                .followSslRedirects(true)
                .retryOnConnectionFailure(true)
                .build()
    }

//    @Bean
//    @Primary
//    fun objectMapper() = jacksonObjectMapper()
}