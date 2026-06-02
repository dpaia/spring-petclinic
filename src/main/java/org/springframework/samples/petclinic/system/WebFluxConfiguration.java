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

import java.nio.charset.StandardCharsets;
import java.time.Instant;

import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.util.HtmlUtils;

import reactor.core.publisher.Mono;

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

	@Bean
	@Order(-2)
	ErrorWebExceptionHandler htmlErrorWebExceptionHandler() {
		return (exchange, ex) -> {
			if (exchange.getResponse().isCommitted()) {
				return Mono.error(ex);
			}
			return acceptsHtml(exchange) ? renderHtmlError(exchange, ex) : renderJsonError(exchange, ex);
		};
	}

	private Mono<Void> renderHtmlError(ServerWebExchange exchange, Throwable ex) {
		String body = """
				<!DOCTYPE html>
				<html><body><h2>Something happened...</h2><p>%s</p></body></html>
				""".formatted(HtmlUtils.htmlEscape(getMessage(ex)));

		exchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
		exchange.getResponse().getHeaders().setContentType(MediaType.TEXT_HTML);
		return writeBody(exchange, body);
	}

	private Mono<Void> renderJsonError(ServerWebExchange exchange, Throwable ex) {
		String body = """
				{"timestamp":"%s","path":"%s","status":500,"error":"Internal Server Error","message":"%s"}
				""".formatted(Instant.now(),
				jsonEscape(exchange.getRequest().getPath().pathWithinApplication().value()),
				jsonEscape(getMessage(ex)));

		exchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
		exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
		return writeBody(exchange, body);
	}

	private Mono<Void> writeBody(ServerWebExchange exchange, String body) {
		return exchange.getResponse()
			.writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8))));
	}

	private String getMessage(Throwable ex) {
		return ex.getMessage() != null ? ex.getMessage() : "Unexpected error";
	}

	private boolean acceptsHtml(ServerWebExchange exchange) {
		return exchange.getRequest()
			.getHeaders()
			.getAccept()
			.stream()
			.anyMatch(mediaType -> MediaType.TEXT_HTML.equalsTypeAndSubtype(mediaType));
	}

	private String jsonEscape(String value) {
		return value.replace("\\", "\\\\").replace("\"", "\\\"");
	}

}
