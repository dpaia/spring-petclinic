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

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.samples.petclinic.system.RestTemplateConfig;
import org.springframework.web.client.RestTemplate;

/**
 * Native-friendly tests for {@link BreedService}. These tests are designed to work in a
 * native image context without using mocks.
 */
@SpringBootTest(classes = BreedServiceNativeTests.TestConfig.class)
class BreedServiceNativeTests {

	/**
	 * Test configuration specifically for native tests to avoid context initializer
	 * conflicts.
	 */
	@Configuration
	@Import(RestTemplateConfig.class)
	static class TestConfig {

		@Bean
		public RestTemplateBuilder restTemplateBuilder() {
			return new RestTemplateBuilder();
		}

		@Bean
		public BreedService breedService(RestTemplate restTemplate) {
			return new BreedService(restTemplate);
		}

	}

	@Autowired
	private BreedService breedService;

	@Test
	void testGetAllBreeds() {
		List<String> breeds = breedService.getAllBreeds();

		// Verify we got a non-empty list of breeds
		assertThat(breeds).isNotNull();
		assertThat(breeds).isNotEmpty();

		// Log the first few breeds for debugging
		System.out.println("[DEBUG_LOG] First 5 breeds: " + breeds.stream().limit(5).toList());
	}

	@Test
	void testGetRandomBreedImage() {
		// Use a common breed that should always exist
		String breed = "labrador";
		String imageUrl = breedService.getRandomBreedImage(breed);

		// Verify we got a valid image URL
		assertThat(imageUrl).isNotNull();
		assertThat(imageUrl).startsWith("https://");
		assertThat(imageUrl).contains("/breeds/");

		// Log the image URL for debugging
		System.out.println("[DEBUG_LOG] Image URL: " + imageUrl);
	}

	@Test
	void testGetRandomBreedImageForNonExistentBreed() {
		// Use a breed that doesn't exist
		String breed = "nonexistentbreed123456789";
		String imageUrl = breedService.getRandomBreedImage(breed);

		// Verify we got null for a non-existent breed
		assertThat(imageUrl).isNull();
	}

}
