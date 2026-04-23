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

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for accessing breed information from an external API using WebClient.
 */
@RestController
@RequestMapping("/api/reactive/breeds")
public class ReactiveBreedController {

	private final ReactiveBreedService reactiveBreedService;

	public ReactiveBreedController(ReactiveBreedService reactiveBreedService) {
		this.reactiveBreedService = reactiveBreedService;
	}

	/**
	 * Get all dog breeds.
	 * @return a list of all dog breeds
	 */
	@GetMapping
	public ResponseEntity<List<String>> getAllBreeds() {
		List<String> breeds = reactiveBreedService.getAllBreeds();
		return ResponseEntity.ok(breeds);
	}

	/**
	 * Get a random image for a specific dog breed.
	 * @param breed the breed name
	 * @return the URL of a random image for the breed
	 */
	@GetMapping("/{breed}/image")
	public ResponseEntity<String> getRandomBreedImage(@PathVariable String breed) {
		String imageUrl = reactiveBreedService.getRandomBreedImage(breed);
		if (imageUrl != null) {
			return ResponseEntity.ok(imageUrl);
		}
		return ResponseEntity.notFound().build();
	}

}
