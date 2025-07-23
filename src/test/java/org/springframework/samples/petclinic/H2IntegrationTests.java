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

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.petclinic.vet.VetRepository;
import org.springframework.web.client.RestTemplate;

/**
 * Integration tests using H2 in-memory database.
 *
 * This test class is a simplified version of the integration tests that doesn't depend on
 * external resources like MySQL or Docker. It uses the default H2 in-memory database
 * configuration provided by Spring Boot.
 *
 * See MYSQL_CONTAINER_FIX.md for details on the issues encountered with MySQL integration
 * tests and the solutions attempted.
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class H2IntegrationTests {

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(H2IntegrationTests.class);

	@LocalServerPort
	int port;

	@Autowired
	private VetRepository vets;

	@Autowired
	private RestTemplateBuilder builder;

	/**
	 * Test that we can retrieve the list of vets from the database.
	 */
	@Test
	void testFindAll() {
		log.info("Running testFindAll");
		vets.findAll();
		vets.findAll(); // served from cache
		log.info("testFindAll completed successfully");
	}

	/**
	 * Test that we can retrieve owner details from the REST API.
	 */
	@Test
	void testOwnerDetails() {
		log.info("Running testOwnerDetails");
		RestTemplate template = builder.rootUri("http://localhost:" + port).build();
		ResponseEntity<String> result = template.exchange(RequestEntity.get("/owners/1").build(), String.class);
		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
		log.info("testOwnerDetails completed successfully");
	}

}
