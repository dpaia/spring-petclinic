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

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

/**
 * Integration Test for {@link CrashController}.
 *
 * @author Alex Lutz
 */
// NOT Waiting https://github.com/spring-projects/spring-boot/issues/5574
@Import(TestEnglishLocaleConfig.class)
@SpringBootTest(webEnvironment = RANDOM_PORT, properties = { "server.error.include-message=ALWAYS" })
class CrashControllerIntegrationTests {

	@Autowired
	private WebTestClient webTestClient;

	@Test
	void testTriggerExceptionJson() {
		webTestClient.get()
			.uri("/oups")
			.accept(MediaType.APPLICATION_JSON)
			.exchange()
			.expectStatus()
			.isOk()
			.expectBody()
			// Since we're returning a view name, even with JSON accept header,
			// we'll get HTML content due to the GlobalErrorHandler
			.xpath("//html")
			.exists()
			.xpath("//body")
			.exists()
			.xpath("//p[contains(text(), 'Expected: controller used to showcase what happens when an exception is thrown')]")
			.exists();
	}

	@Test
	void testTriggerExceptionHtml() {
		webTestClient.get()
			.uri("/oups")
			.accept(MediaType.TEXT_HTML)
			.exchange()
			.expectStatus()
			.isOk()
			.expectBody()
			.xpath("//html")
			.exists()
			.xpath("//body")
			.exists()
			.xpath("//h2[contains(text(), 'Something happened')]")
			.exists()
			.xpath("//p[contains(text(), 'Expected: controller used to showcase what happens when an exception is thrown')]")
			.exists()
			// Not the whitelabel error page
			.xpath("//h1[contains(text(), 'Whitelabel Error Page')]")
			.doesNotExist()
			.xpath("//*[contains(text(), 'This application has no explicit mapping for')]")
			.doesNotExist();
	}

	@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class,
			DataSourceTransactionManagerAutoConfiguration.class, HibernateJpaAutoConfiguration.class })
	static class TestConfiguration {

	}

}
