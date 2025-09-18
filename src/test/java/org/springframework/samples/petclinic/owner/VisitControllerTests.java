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

package org.springframework.samples.petclinic.owner;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledInNativeImage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.samples.petclinic.system.TestEnglishLocaleConfig;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.aot.DisabledInAotMode;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Test class for {@link VisitController}
 *
 * @author Colin But
 * @author Wick Dynex
 */
@Import(TestEnglishLocaleConfig.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisabledInNativeImage
@DisabledInAotMode
class VisitControllerTests {

	private static final int TEST_OWNER_ID = 1;

	private static final int TEST_PET_ID = 1;

	@Autowired
	private WebTestClient webTestClient;

	@Test
	void testInitNewVisitForm() {
		webTestClient.get()
			.uri("/owners/{ownerId}/pets/{petId}/visits/new", TEST_OWNER_ID, TEST_PET_ID)
			.exchange()
			.expectStatus()
			.isOk()
			.expectBody()
			.xpath("//form[@class='form-horizontal']")
			.exists()
			.xpath("//input[@id='date']/@value")
			.exists()
			.xpath("//input[@id='description']")
			.exists();
	}

	@Test
	@DirtiesContext
	void testProcessNewVisitFormSuccess() {
		webTestClient.post()
			.uri("/owners/{ownerId}/pets/{petId}/visits/new", TEST_OWNER_ID, TEST_PET_ID)
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.bodyValue("date=2025-08-01&description=Visit+Description")
			.exchange()
			.expectStatus()
			.is3xxRedirection()
			.expectHeader()
			.valueEquals("Location", "/owners/" + TEST_OWNER_ID);
	}

	@Test
	void testProcessNewVisitFormHasErrors() {
		webTestClient.post()
			.uri("/owners/{ownerId}/pets/{petId}/visits/new", TEST_OWNER_ID, TEST_PET_ID)
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.bodyValue("date=2025-08-01")
			.exchange()
			.expectStatus()
			.isOk()
			.expectBody()
			.xpath("//form[@class='form-horizontal']")
			.exists()
			.xpath("//input[@id='date']/@value")
			.isEqualTo("2025-08-01")
			.xpath("//input[@id='description']/ancestor::div[contains(@class, 'has-error')]")
			.exists()
			.xpath("//input[@id='description']/ancestor::div//span[@class='help-inline'][contains(text(), 'must not be blank')]")
			.exists();
	}

}
