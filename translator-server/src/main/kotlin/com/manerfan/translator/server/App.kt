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

package com.manerfan.translator.server

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import com.manerfan.translator.server.interceptors.AccessLogInterceptor
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.*
import java.io.File


/**
 * @author manerfan
 * @date 2017/12/24
 */

@SpringBootApplication
class App : InitializingBean {
    @Value("\${server.data.doc.dir}")
    lateinit var docDir: File

    override fun afterPropertiesSet() {
        docDir.mkdirs()
    }

}

fun main(args: Array<String>) {
    SpringApplication.run(App::class.java, * args)
}

@Configuration
//@EnableWebMvc
class WebMvcConfiguration : WebMvcConfigurer {
    @Autowired
    lateinit var accessLogInterceptor: AccessLogInterceptor

    override fun addViewControllers(registry: ViewControllerRegistry) {
        registry.addRedirectViewController("/", "/index.html")
    }

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(accessLogInterceptor).excludePathPatterns("*.html", "**/static/**")
    }
}
