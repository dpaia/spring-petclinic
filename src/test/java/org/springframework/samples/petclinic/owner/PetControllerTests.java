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

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledInNativeImage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.samples.petclinic.system.TestEnglishLocaleConfig;
import org.springframework.test.context.aot.DisabledInAotMode;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.LocalDate;
import java.util.function.Consumer;

/**
 * Test class for the {@link PetController}
 *
 * @author Colin But
 * @author Wick Dynex
 */
@Import(TestEnglishLocaleConfig.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisabledInNativeImage
@DisabledInAotMode
class PetControllerTests {

	private static final int TEST_OWNER_ID = 1;

	private static final int TEST_PET_ID = 1;

	@Autowired
	private WebTestClient webTestClient;

	@Test
	void testInitCreationForm() {
		webTestClient.get()
			.uri("/owners/{ownerId}/pets/new", TEST_OWNER_ID)
			.exchange()
			.expectStatus()
			.isOk()
			.expectBody()
			.xpath("//input[@id='name']")
			.exists()
			.xpath("//select[@id='type']")
			.exists()
			.xpath("//input[@id='birthDate']")
			.exists();
	}

	@Test
	void testProcessCreationFormSuccess() {
		webTestClient.post()
			.uri("/owners/{ownerId}/pets/new", TEST_OWNER_ID)
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.bodyValue("name=Betty&type=hamster&birthDate=2015-02-12")
			.exchange()
			.expectStatus()
			.is3xxRedirection()
			.expectHeader()
			.valueEquals("Location", "/owners/" + TEST_OWNER_ID);
	}

	@Nested
	class ProcessCreationFormHasErrors {

		@Test
		void testProcessCreationFormWithBlankName() {
			webTestClient.post()
				.uri("/owners/{ownerId}/pets/new", TEST_OWNER_ID)
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.bodyValue("name=&birthDate=2015-02-12")
				.exchange()
				.expectStatus()
				.isOk()
				.expectBody()
				.xpath("//input[@id='name']/@value")
				.isEqualTo("")
				.xpath("//input[@id='birthDate']/@value")
				.isEqualTo("2015-02-12")
				.xpath("//div[label[@for='name']]/div/span[contains(@class, 'help-inline') and text()='is required']")
				.exists()
				.xpath("//div[label[@for='type']]/div/span[contains(@class, 'help-inline') and text()='is required']")
				.exists();
		}

		@Test
		void testProcessCreationFormWithDuplicateName() {
			webTestClient.post()
				.uri("/owners/{ownerId}/pets/new", TEST_OWNER_ID)
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.bodyValue("name=Leo&birthDate=2015-02-12&type=hamster")
				.exchange()
				.expectStatus()
				.isOk()
				.expectBody()
				.xpath("//input[@id='name']/@value")
				.isEqualTo("Leo")
				.xpath("//input[@id='birthDate']/@value")
				.isEqualTo("2015-02-12")
				.xpath("//input[@id='name']/following::span[contains(@class, 'help-inline')][1]/text()")
				.isEqualTo("is already in use");
		}

		@Test
		void testProcessCreationFormWithMissingPetType() {
			webTestClient.post()
				.uri("/owners/{ownerId}/pets/new", TEST_OWNER_ID)
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.bodyValue("name=Betty&birthDate=2015-02-12")
				.exchange()
				.expectStatus()
				.isOk()
				.expectBody()
				.xpath("//input[@id='name']/@value")
				.isEqualTo("Betty")
				.xpath("//input[@id='birthDate']/@value")
				.isEqualTo("2015-02-12")
				.xpath("//div[label[@for='type']]/div/span[contains(@class, 'help-inline') and text()='is required']")
				.exists();
		}

		@Test
		void testProcessCreationFormWithInvalidBirthDate() {
			LocalDate currentDate = LocalDate.now();
			String futureBirthDate = currentDate.plusMonths(1).toString();

			webTestClient.post()
				.uri("/owners/{ownerId}/pets/new", TEST_OWNER_ID)
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.bodyValue("name=Betty&birthDate=" + futureBirthDate)
				.exchange()
				.expectStatus()
				.isOk()
				.expectBody()
				.xpath("//input[@id='name']/@value")
				.isEqualTo("Betty")
				.xpath("//input[@id='birthDate']/@value")
				.isEqualTo(futureBirthDate)
				.xpath("//div[label[@for='birthDate']]/div/span[contains(@class, 'help-inline') and text()='invalid date']")
				.exists();
		}

		@Test
		void testInitUpdateForm() {
			webTestClient.get()
				.uri("/owners/{ownerId}/pets/{petId}/edit", TEST_OWNER_ID, TEST_PET_ID)
				.exchange()
				.expectStatus()
				.isOk()
				.expectBody()
				.xpath("//input[@id='name']/@value")
				.isEqualTo("Leo")
				.xpath("//select[@id='type']/option[@selected='selected']")
				.exists()
				.xpath("//input[@id='birthDate']/@value")
				.isEqualTo("2010-09-07");
		}

	}

	@Test
	void testProcessUpdateFormSuccess() {
		Consumer<String> renameToName = name -> {
			webTestClient.post()
				.uri("/owners/{ownerId}/pets/{petId}/edit", TEST_OWNER_ID, TEST_PET_ID)
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.bodyValue("name=" + name + "&type=hamster&birthDate=2010-09-07")
				.exchange()
				.expectStatus()
				.is3xxRedirection()
				.expectHeader()
				.valueEquals("Location", "/owners/" + TEST_OWNER_ID);
		};
		renameToName.accept("Leo1");
		renameToName.accept("Leo");
	}

	@Nested
	class ProcessUpdateFormHasErrors {

		@Test
		void testProcessUpdateFormWithInvalidBirthDate() {
			webTestClient.post()
				.uri("/owners/{ownerId}/pets/{petId}/edit", TEST_OWNER_ID, TEST_PET_ID)
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.bodyValue("name=Betty&birthDate=2015/02/12")
				.exchange()
				.expectStatus()
				.isOk()
				.expectBody()
				.xpath("//input[@id='name']/@value")
				.isEqualTo("Betty")
				.xpath("//input[@id='birthDate']/@value")
				.isEqualTo("2015/02/12");
		}

		@Test
		void testProcessUpdateFormWithBlankName() {
			webTestClient.post()
				.uri("/owners/{ownerId}/pets/{petId}/edit", TEST_OWNER_ID, TEST_PET_ID)
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.bodyValue("name=&birthDate=2015-02-12")
				.exchange()
				.expectStatus()
				.isOk()
				.expectBody()
				.xpath("//form[@class='form-horizontal']")
				.exists()
				.xpath("//input[@id='name']")
				.exists()
				.xpath("//input[@id='name']/@value")
				.isEqualTo("")
				.xpath("//input[@id='birthDate']/@value")
				.isEqualTo("2015-02-12");
		}

	}

}
