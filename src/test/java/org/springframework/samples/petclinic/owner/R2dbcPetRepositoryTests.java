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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import reactor.test.StepVerifier;

import java.util.Set;

@DataR2dbcTest
class R2dbcPetRepositoryTests {

	@Autowired
	private PetRepository petRepository;

	@Test
	void shouldFindPetsByOwnerId() {
		petRepository.findByOwnerId(6).collectList().as(StepVerifier::create).expectNextMatches(pets -> {
			if (pets == null || pets.isEmpty())
				return false;
			Set<String> names = pets.stream().map(Pet::getName).collect(java.util.stream.Collectors.toSet());
			return names.contains("Samantha") && names.contains("Max") && pets.size() == 2;
		}).verifyComplete();
	}

	@Test
	void shouldFindPetByIdAndOwnerId() {
		petRepository.findByIdAndOwnerId(7, 6)
			.as(StepVerifier::create)
			.expectNextMatches(
					pet -> pet != null && pet.getId() == 7 && pet.getOwnerId() == 6 && "Samantha".equals(pet.getName()))
			.verifyComplete();
	}

}
