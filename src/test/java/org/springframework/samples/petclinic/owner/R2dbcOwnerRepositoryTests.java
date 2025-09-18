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
import org.springframework.data.domain.PageRequest;
import reactor.test.StepVerifier;

@DataR2dbcTest
class R2dbcOwnerRepositoryTests {

	@Autowired
	private OwnerRepository ownerRepository;

	@Test
	void shouldFindOwnersByLastName() {
		// Create a test owner
		Owner owner = new Owner();
		owner.setFirstName("John");
		owner.setLastName("Doe");
		owner.setAddress("123 Main St");
		owner.setCity("Anytown");
		owner.setTelephone("1234567890");

		// Save the owner and then find by last name
		ownerRepository.save(owner)
			.then(ownerRepository.findByLastNameStartingWith("Doe", PageRequest.of(0, 10)).collectList())
			.as(StepVerifier::create)
			.expectNextMatches(owners -> {
				// Verify we found at least one owner with last name starting with "Doe"
				return !owners.isEmpty() && owners.stream().anyMatch(o -> o.getLastName().startsWith("Doe"));
			})
			.verifyComplete();
	}

	@Test
	void shouldFindOwnerById() {
		// Create a test owner
		Owner owner = new Owner();
		owner.setFirstName("Jane");
		owner.setLastName("Smith");
		owner.setAddress("456 Oak St");
		owner.setCity("Somewhere");
		owner.setTelephone("0987654321");

		// Save the owner and then find by ID
		ownerRepository.save(owner)
			.flatMap(savedOwner -> ownerRepository.findById(savedOwner.getId()))
			.as(StepVerifier::create)
			.expectNextMatches(foundOwner -> foundOwner.getFirstName().equals("Jane")
					&& foundOwner.getLastName().equals("Smith") && foundOwner.getAddress().equals("456 Oak St")
					&& foundOwner.getCity().equals("Somewhere") && foundOwner.getTelephone().equals("0987654321"))
			.verifyComplete();
	}

}
