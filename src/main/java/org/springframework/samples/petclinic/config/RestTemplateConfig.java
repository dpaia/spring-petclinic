/*
 * Copyright 2012-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.samples.petclinic.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Configuration
public class RestTemplateConfig {

	@Bean
	public RestTemplate customRestTemplate(RestTemplateBuilder builder) {
		return builder.connectTimeout(Duration.ofSeconds(30)).readTimeout(Duration.ofSeconds(60))
				.rootUri("https://petclinic-api.example.com").build();
	}

	@Bean("externalApiRestTemplate")
	public RestTemplate externalApiRestTemplate(RestTemplateBuilder builder) {
		return builder.connectTimeout(Duration.ofSeconds(10)).readTimeout(Duration.ofSeconds(30))
				.rootUri("https://api.example.com").build();
	}

}
