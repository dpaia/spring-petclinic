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

package org.springframework.samples.petclinic.vet;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledInNativeImage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.samples.petclinic.system.TestEnglishLocaleConfig;
import org.springframework.test.context.aot.DisabledInAotMode;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Test class for the {@link VetController}
 */
@Import(TestEnglishLocaleConfig.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DisabledInNativeImage
@DisabledInAotMode
class VetControllerTests {

	@Autowired
	private WebTestClient webTestClient;

	@Test
	void testShowVetListHtml() {
		webTestClient.get()
			.uri("/vets.html?page=1")
			.exchange()
			.expectStatus()
			.isOk()
			.expectBody()
			.xpath("//h2[contains(text(), 'Veterinarians')]")
			.exists()
			.xpath("//table[@id='vets']")
			.exists();
	}

	@Test
	void testShowResourcesVetList() {
		webTestClient.get()
			.uri("/vets")
			.accept(MediaType.APPLICATION_JSON)
			.exchange()
			.expectStatus()
			.isOk()
			.expectHeader()
			.contentType(MediaType.APPLICATION_JSON)
			.expectBody()
			.jsonPath("$.vetList[0].id")
			.isEqualTo(1);
	}

}
