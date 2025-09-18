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
package org.springframework.samples.petclinic.breed;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.match.MockRestRequestMatchers;
import org.springframework.test.web.client.response.MockRestResponseCreators;
import org.springframework.web.client.RestTemplate;

/**
 * Test class for {@link BreedService}
 */
class BreedServiceTests {

	private BreedService breedService;

	private MockRestServiceServer mockServer;

	private RestTemplate restTemplate;

	@BeforeEach
	void setup() {
		restTemplate = new RestTemplateBuilder().build();
		breedService = new BreedService(restTemplate);
		mockServer = MockRestServiceServer.createServer(restTemplate);
	}

	@Test
	void testGetAllBreeds() {
		// Setup mock response
		mockServer.expect(MockRestRequestMatchers.requestTo("https://dog.ceo/api/breeds/list"))
			.andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
			.andRespond(MockRestResponseCreators.withStatus(HttpStatus.OK)
				.contentType(MediaType.APPLICATION_JSON)
				.body("{\"status\":\"success\",\"message\":[\"affenpinscher\",\"african\",\"airedale\"]}"));

		// Execute the service call
		List<String> breeds = breedService.getAllBreeds();

		// Verify the response
		assertThat(breeds).isNotNull();
		assertThat(breeds).hasSize(3);
		assertThat(breeds).contains("affenpinscher", "african", "airedale");

		// Verify all expectations met
		mockServer.verify();
	}

	@Test
	void testGetAllBreedsWhenApiReturnsError() {
		// Setup mock response
		mockServer.expect(MockRestRequestMatchers.requestTo("https://dog.ceo/api/breeds/list"))
			.andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
			.andRespond(MockRestResponseCreators.withStatus(HttpStatus.OK)
				.contentType(MediaType.APPLICATION_JSON)
				.body("{\"status\":\"error\",\"message\":\"Something went wrong\"}"));

		// Execute the service call
		List<String> breeds = breedService.getAllBreeds();

		// Verify the response
		assertThat(breeds).isNotNull();
		assertThat(breeds).isEmpty();

		// Verify all expectations met
		mockServer.verify();
	}

	@Test
	void testGetRandomBreedImage() {
		// Setup mock response
		mockServer.expect(MockRestRequestMatchers.requestTo("https://dog.ceo/api/breed/labrador/images/random"))
			.andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
			.andRespond(MockRestResponseCreators.withStatus(HttpStatus.OK)
				.contentType(MediaType.APPLICATION_JSON)
				.body("{\"status\":\"success\",\"message\":\"https://images.dog.ceo/breeds/labrador/n02099712_1.jpg\"}"));

		// Execute the service call
		String imageUrl = breedService.getRandomBreedImage("labrador");

		// Verify the response
		assertThat(imageUrl).isNotNull();
		assertThat(imageUrl).isEqualTo("https://images.dog.ceo/breeds/labrador/n02099712_1.jpg");

		// Verify all expectations met
		mockServer.verify();
	}

	@Test
	void testGetRandomBreedImageWhenApiReturnsError() {
		// Setup mock response
		mockServer.expect(MockRestRequestMatchers.requestTo("https://dog.ceo/api/breed/unknown/images/random"))
			.andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
			.andRespond(MockRestResponseCreators.withStatus(HttpStatus.OK)
				.contentType(MediaType.APPLICATION_JSON)
				.body("{\"status\":\"error\",\"message\":\"Breed not found\"}"));

		// Execute the service call
		String imageUrl = breedService.getRandomBreedImage("unknown");

		// Verify the response
		assertThat(imageUrl).isNull();

		// Verify all expectations met
		mockServer.verify();
	}

	@Test
	void testGetRandomBreedImageWhenApiThrowsException() {
		// Setup mock response
		mockServer.expect(MockRestRequestMatchers.requestTo("https://dog.ceo/api/breed/error/images/random"))
			.andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
			.andRespond(MockRestResponseCreators.withStatus(HttpStatus.INTERNAL_SERVER_ERROR));

		// Execute the service call
		String imageUrl = breedService.getRandomBreedImage("error");

		// Verify the response
		assertThat(imageUrl).isNull();

		// Verify all expectations met
		mockServer.verify();
	}

}
