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
package org.springframework.samples.petclinic.profile;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.petclinic.vet.Vets;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class to validate the profiling methodology. This ensures that the REST API
 * functions correctly and can be used for profiling.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProfileValidationTest {

	@Autowired
	private TestRestTemplate restTemplate;

	/**
	 * Test that the /vets endpoint returns a valid response. This is the endpoint used in
	 * the profiling scripts.
	 */
	@Test
	void testVetsEndpoint() {
		ResponseEntity<Vets> response = restTemplate.getForEntity("/vets", Vets.class);

		// Verify HTTP status is OK
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

		// Verify response body is not null
		assertThat(response.getBody()).isNotNull();

		// Verify the list of vets is not null
		assertThat(response.getBody().getVetList()).isNotNull();

		// Verify the list of vets is not empty
		assertThat(response.getBody().getVetList()).isNotEmpty();

		// Log the number of vets for debugging
		System.out.println("[DEBUG_LOG] Number of vets: " + response.getBody().getVetList().size());
	}

	/**
	 * Test that the /actuator/health endpoint returns UP status. This endpoint is used in
	 * the profiling scripts to determine when the application is ready.
	 */
	@Test
	void testHealthEndpoint() {
		ResponseEntity<String> response = restTemplate.getForEntity("/actuator/health", String.class);

		// Verify HTTP status is OK
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

		// Verify response body is not null
		assertThat(response.getBody()).isNotNull();

		// Verify the health status is UP
		assertThat(response.getBody()).contains("UP");

		System.out.println("[DEBUG_LOG] Health response: " + response.getBody());
	}

	/**
	 * Test the performance of the /vets endpoint under load. This simulates the load
	 * testing done in the profiling scripts.
	 */
	@Test
	void testVetsEndpointPerformance() {
		long startTime = System.currentTimeMillis();

		// Make multiple requests to simulate load
		for (int i = 0; i < 10; i++) {
			ResponseEntity<Vets> response = restTemplate.getForEntity("/vets", Vets.class);
			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		}

		long endTime = System.currentTimeMillis();
		long duration = endTime - startTime;

		// Log the average response time
		System.out.println("[DEBUG_LOG] Average response time for /vets: " + (duration / 10) + "ms");

		// Ensure the average response time is reasonable (less than 500ms per request)
		assertThat(duration / 10).isLessThan(500);
	}

}
