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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * Service to fetch dog breed information from an external API.
 */
@Service
public class BreedService {

	private static final Logger logger = LoggerFactory.getLogger(BreedService.class);

	private static final String DOG_API_URL = "https://dog.ceo/api";

	private final RestTemplate restTemplate;

	public BreedService(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	/**
	 * Fetches a list of all dog breeds from the Dog API.
	 * @return a list of breed names
	 */
	public List<String> getAllBreeds() {
		try {
			// Use URI template instead of string concatenation
			String url = DOG_API_URL + "/breeds/list";
			BreedListResponse response = restTemplate.getForObject(url, BreedListResponse.class);
			if (response != null && response.getStatus().equals("success")) {
				return response.getMessage();
			}
			logger.warn("Failed to get breeds: {}", response != null ? response.getStatus() : "null response");
			return Collections.emptyList();
		}
		catch (RestClientException e) {
			logger.error("Error fetching breeds from external API", e);
			return Collections.emptyList();
		}
	}

	/**
	 * Fetches a random image URL for a specific dog breed.
	 * @param breed the breed name
	 * @return the URL of a random image for the breed, or null if not found
	 */
	public String getRandomBreedImage(String breed) {
		try {
			// Use URI template with explicit variable
			String url = DOG_API_URL + "/breed/{breed}/images/random";
			BreedImageResponse response = restTemplate.getForObject(url, BreedImageResponse.class, breed);
			if (response != null && response.getStatus().equals("success")) {
				return response.getMessage();
			}
			logger.warn("Failed to get breed image: {}", response != null ? response.getStatus() : "null response");
			return null;
		}
		catch (RestClientException e) {
			logger.error("Error fetching breed image from external API", e);
			return null;
		}
	}

	/**
	 * Response class for the breed list API endpoint.
	 */
	public static class BreedListResponse {

		private String status;

		private List<String> message;

		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}

		public List<String> getMessage() {
			return message;
		}

		public void setMessage(List<String> message) {
			this.message = message;
		}

	}

	/**
	 * Response class for the breed image API endpoint.
	 */
	public static class BreedImageResponse {

		private String status;

		private String message;

		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}

	}

}
