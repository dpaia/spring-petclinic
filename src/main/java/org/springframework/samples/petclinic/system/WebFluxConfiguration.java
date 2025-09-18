/*
 * Copyright 2012-2025 the original author or authors.
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
package org.springframework.samples.petclinic.system;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.server.WebFilter;

/**
 * Configures WebFlux-specific settings for the application.
 *
 * <p>
 * Provides configuration for reactive web applications, including resource handling, CORS
 * support, and other WebFlux-specific settings.
 * </p>
 *
 * <p>
 * This configuration works alongside Spring Boot's auto-configuration for WebFlux. The
 * locale context resolver is intentionally commented out to avoid conflicts with Spring's
 * DelegatingWebFluxConfiguration, which already provides a bean with the same name.
 * </p>
 *
 * <p>
 * We also avoid using @EnableWebFlux to prevent conflicts with Spring Boot's
 * auto-configuration, which already sets up the WebFlux environment appropriately.
 * </p>
 *
 * @author Junie
 */
@Configuration
public class WebFluxConfiguration implements WebFluxConfigurer {

	/**
	 * Creates a WebFilter that adds CORS support for the WebFlux application. This allows
	 * cross-origin requests from browsers to access the API.
	 * @return a WebFilter that handles CORS
	 */
	@Bean
	public WebFilter corsFilter() {
		return (exchange, chain) -> {
			exchange.getResponse().getHeaders().add("Access-Control-Allow-Origin", "*");
			exchange.getResponse().getHeaders().add("Access-Control-Allow-Methods", "GET, PUT, POST, DELETE, OPTIONS");
			exchange.getResponse().getHeaders().add("Access-Control-Allow-Headers", "Content-Type, Authorization");
			return chain.filter(exchange);
		};
	}

}
