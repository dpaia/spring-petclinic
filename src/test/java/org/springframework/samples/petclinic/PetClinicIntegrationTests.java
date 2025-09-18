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

package org.springframework.samples.petclinic;

import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.samples.petclinic.vet.VetRepository;
import org.springframework.test.context.aot.DisabledInAotMode;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DisabledInAotMode
public class PetClinicIntegrationTests {

	@Autowired
	private VetRepository vets;

	@Autowired
	private WebTestClient webTestClient;

	@Test
	void testFindAll() {
		// First call to populate cache
		var allVets = vets.findAll().collectList().block();
		var cachedVets = vets.findAll().collectList().block();

		for (int i = 0; i < allVets.size(); i++) {
			assertSame(allVets.get(i), cachedVets.get(i));
		}
	}

	@Test
	void testOwnerDetails() {
		webTestClient.get()
			.uri("/owners/1")
			.exchange()
			.expectStatus()
			.isOk()
			.expectHeader()
			.contentTypeCompatibleWith(MediaType.TEXT_HTML);
	}

	public static void main(String[] args) {
		SpringApplication.run(PetClinicApplication.class, args);
	}

}
