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

import org.junit.jupiter.api.Assertions;
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
import org.springframework.web.reactive.function.BodyInserters;

/**
 * Test class for {@link OwnerController}
 *
 * @author Colin But
 * @author Wick Dynex
 */
@Import(TestEnglishLocaleConfig.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisabledInNativeImage
@DisabledInAotMode
class OwnerControllerTests {

	private static final int TEST_OWNER_ID = 1;

	@Autowired
	private WebTestClient webTestClient;

	@Test
	void testInitCreationForm() {
		webTestClient.get()
			.uri("/owners/new")
			.exchange()
			.expectStatus()
			.isOk()
			.expectBody()
			.xpath("//input[@name='firstName']")
			.exists();
	}

	@Test
	@DirtiesContext
	void testProcessCreationFormSuccess() {
		webTestClient.post()
			.uri("/owners/new")
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.bodyValue("firstName=Joe&lastName=Bloggs&address=123 Caramel Street&city=London&telephone=1316761638")
			.exchange()
			.expectStatus()
			.is3xxRedirection();
	}

	@Test
	void testProcessCreationFormHasErrors() {
		webTestClient.post()
			.uri("/owners/new")
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.bodyValue("firstName=Joe&lastName=Bloggs&city=London")
			.exchange()
			.expectStatus()
			.isOk()
			.expectBody()
			.xpath("//span[@class='help-inline'][contains(text(), 'must not be blank')]")
			.exists();
	}

	@Test
	void testInitFindForm() {
		webTestClient.get()
			.uri("/owners/find")
			.exchange()
			.expectStatus()
			.isOk()
			.expectBody()
			.xpath("//form[@id='search-owner-form']")
			.exists();
	}

	@Test
	void testProcessFindFormSuccess() {
		webTestClient.get()
			.uri("/owners?page=1")
			.exchange()
			.expectStatus()
			.isOk()
			.expectBody()
			.xpath("//h2[contains(text(), 'Owners')]")
			.exists()
			.xpath("//table[@id='owners']//tr[td[a[contains(text(), 'George Franklin')]]]")
			.exists();
	}

	@Test
	void testProcessFindFormByLastName() {
		webTestClient.get()
			.uri("/owners?page=1&lastName=Franklin")
			.exchange()
			.expectStatus()
			.is3xxRedirection()
			.expectHeader()
			.valueEquals("Location", "/owners/" + TEST_OWNER_ID);
	}

	@Test
	void testProcessFindFormNoOwnersFound() {
		webTestClient.get()
			.uri("/owners?page=1&lastName=Unknown Surname")
			.exchange()
			.expectStatus()
			.isOk()
			.expectBody()
			.xpath("//span[@class='help-inline']//p[contains(text(), 'has not been found')]")
			.exists();
	}

	@Test
	void testInitUpdateOwnerForm() {
		webTestClient.get()
			.uri("/owners/{ownerId}/edit", TEST_OWNER_ID)
			.exchange()
			.expectStatus()
			.isOk()
			.expectBody()
			.xpath("//input[@name='firstName'][@value='George']")
			.exists()
			.xpath("//input[@name='lastName'][@value='Franklin']")
			.exists()
			.xpath("//input[@name='address'][@value='110 W. Liberty St.']")
			.exists()
			.xpath("//input[@name='city'][@value='Madison']")
			.exists()
			.xpath("//input[@name='telephone'][@value='6085551023']")
			.exists();
	}

	@Test
	@DirtiesContext
	void testProcessUpdateOwnerFormSuccess() {
		webTestClient.post()
			.uri("/owners/{ownerId}/edit", 2)
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.bodyValue("firstName=Joe&lastName=Bloggs&address=123 Caramel Street&city=London&telephone=1616291589")
			.exchange()
			.expectStatus()
			.is3xxRedirection()
			.expectHeader()
			.valueEquals("Location", "/owners/" + 2);
	}

	@Test
	void testProcessUpdateOwnerFormUnchangedSuccess() {
		webTestClient.post()
			.uri("/owners/{ownerId}/edit", TEST_OWNER_ID)
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.bodyValue(
					"firstName=George&lastName=Franklin&address=110 W. Liberty St.&city=Madison&telephone=6085551023")
			.exchange()
			.expectStatus()
			.is3xxRedirection()
			.expectHeader()
			.valueEquals("Location", "/owners/" + TEST_OWNER_ID);
	}

	@Test
	void testProcessUpdateOwnerFormHasErrors() {
		webTestClient.post()
			.uri("/owners/{ownerId}/edit", 2)
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.body(BodyInserters.fromFormData("firstName", "Joe")
				.with("lastName", "Bloggs")
				.with("address", "")
				.with("telephone", ""))
			.exchange()
			.expectStatus()
			.isOk()
			.expectHeader()
			.contentTypeCompatibleWith(MediaType.TEXT_HTML)
			.expectBody()
			.xpath("//input[@id='firstName']/@value")
			.isEqualTo("Joe")
			.xpath("//input[@id='lastName']/@value")
			.isEqualTo("Bloggs")
			.xpath("//input[@id='address']/@value")
			.isEqualTo("")
			.xpath("//input[@id='telephone']/@value")
			.isEqualTo("")
			.xpath("//input[@id='address']/ancestor::div[contains(@class, 'col-sm-10')]//span[@class='help-inline']")
			.isEqualTo("must not be blank")
			.xpath("//input[@id='telephone']/ancestor::div[contains(@class, 'col-sm-10')]//span[@class='help-inline']")
			.string(error -> {
				Assertions.assertTrue(error.contains("must not be blank"));
				Assertions.assertTrue(error.contains("Telephone must be a 10-digit number"));
			});
	}

	@Test
	void testShowOwner() {
		webTestClient.get()
			.uri("/owners/{ownerId}", TEST_OWNER_ID)
			.exchange()
			.expectStatus()
			.isOk()
			.expectBody()
			.xpath("//th[normalize-space(text())='Name']/following-sibling::td[b[normalize-space(text())='George Franklin']]")
			.exists()
			.xpath("//th[normalize-space(text())='Address']/following-sibling::td[normalize-space(text())='110 W. Liberty St.']")
			.exists()
			.xpath("//th[normalize-space(text())='City']/following-sibling::td[normalize-space(text())='Madison']")
			.exists()
			.xpath("//th[normalize-space(text())='Telephone']/following-sibling::td[normalize-space(text())='6085551023']")
			.exists()
			.xpath("//dt[normalize-space(text())='Name']/following-sibling::dd[1][normalize-space(text())='Leo']")
			.exists();
	}

	@Test
	void testPageWithoutPagination() {
		webTestClient.get()
			.uri("/owners?page=1&lastName=Davis")
			.exchange()
			.expectStatus()
			.isOk()
			.expectBody()
			.xpath("//a[@href='/owners?page=1' and text()='1']")
			.doesNotExist();
	}

	@Test
	@DirtiesContext
	void testPageWithPaginationWithNumberNotMultipleOf5() {
		webTestClient.post()
			.uri("/owners/new")
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.bodyValue("firstName=Joe&lastName=Bloggs&address=123 Caramel Street&city=London&telephone=1316761638")
			.exchange()
			.expectStatus()
			.is3xxRedirection();

		webTestClient.get()
			.uri("/owners")
			.exchange()
			.expectStatus()
			.isOk()
			.expectBody()
			.xpath("//a[@href='/owners?page=3' and text()='3']")
			.exists();
	}

}
