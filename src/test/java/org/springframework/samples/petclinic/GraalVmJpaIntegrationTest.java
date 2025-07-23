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

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.samples.petclinic.owner.OwnerRepository;
import org.springframework.samples.petclinic.owner.Pet;
import org.springframework.samples.petclinic.owner.PetType;
import org.springframework.samples.petclinic.owner.PetTypeRepository;
import org.springframework.samples.petclinic.owner.Visit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration test specifically designed to verify JPA functionality in a GraalVM native
 * image. Tests CRUD operations and relationships to ensure Hibernate/JPA works correctly
 * with reflection hints.
 */
@SpringBootTest
@Transactional
public class GraalVmJpaIntegrationTest {

	@Autowired
	private OwnerRepository ownerRepository;

	@Autowired
	private PetTypeRepository petTypeRepository;

	/**
	 * Test creating a new owner and retrieving it by ID. This verifies basic CRUD
	 * operations work in a native image.
	 */
	@Test
	void testCreateAndRetrieveOwner() {
		// Create a new owner
		Owner owner = new Owner();
		owner.setFirstName("John");
		owner.setLastName("Doe");
		owner.setAddress("123 Main St");
		owner.setCity("Anytown");
		owner.setTelephone("1234567890");

		// Save the owner
		ownerRepository.save(owner);

		// Verify the owner was saved with an ID
		assertThat(owner.getId()).isNotNull();

		// Retrieve the owner by ID
		Owner foundOwner = ownerRepository.findById(owner.getId()).orElse(null);
		assertThat(foundOwner).isNotNull();
		assertThat(foundOwner.getFirstName()).isEqualTo("John");
		assertThat(foundOwner.getLastName()).isEqualTo("Doe");
	}

	/**
	 * Test updating an existing owner. This verifies that updates work correctly in a
	 * native image.
	 */
	@Test
	void testUpdateOwner() {
		// Create a new owner
		Owner owner = new Owner();
		owner.setFirstName("Jane");
		owner.setLastName("Smith");
		owner.setAddress("456 Oak St");
		owner.setCity("Somewhere");
		owner.setTelephone("0987654321");

		// Save the owner
		ownerRepository.save(owner);

		// Update the owner
		owner.setFirstName("Janet");
		owner.setCity("Elsewhere");
		ownerRepository.save(owner);

		// Retrieve the updated owner
		Owner updatedOwner = ownerRepository.findById(owner.getId()).orElse(null);
		assertThat(updatedOwner).isNotNull();
		assertThat(updatedOwner.getFirstName()).isEqualTo("Janet");
		assertThat(updatedOwner.getCity()).isEqualTo("Elsewhere");
	}

	/**
	 * Test finding owners by last name. This verifies that queries work correctly in a
	 * native image.
	 */
	@Test
	void testFindOwnersByLastName() {
		// Create owners with the same last name
		Owner owner1 = new Owner();
		owner1.setFirstName("Alice");
		owner1.setLastName("Johnson");
		owner1.setAddress("789 Pine St");
		owner1.setCity("Somewhere");
		owner1.setTelephone("1112223333");

		Owner owner2 = new Owner();
		owner2.setFirstName("Bob");
		owner2.setLastName("Johnson");
		owner2.setAddress("101 Cedar St");
		owner2.setCity("Elsewhere");
		owner2.setTelephone("4445556666");

		// Save the owners
		ownerRepository.save(owner1);
		ownerRepository.save(owner2);

		// Find owners by last name
		Pageable pageable = PageRequest.of(0, 10);
		Page<Owner> ownersPage = ownerRepository.findByLastNameStartingWith("Johnson", pageable);
		List<Owner> owners = ownersPage.getContent();
		assertThat(owners).hasSize(2);
		assertThat(owners).extracting(Owner::getFirstName).containsExactlyInAnyOrder("Alice", "Bob");
	}

	/**
	 * Test creating and retrieving a pet with its owner. This verifies that relationships
	 * work correctly in a native image.
	 */
	@Test
	void testPetRelationship() {
		// Create an owner
		Owner owner = new Owner();
		owner.setFirstName("Charlie");
		owner.setLastName("Brown");
		owner.setAddress("202 Elm St");
		owner.setCity("Hometown");
		owner.setTelephone("7778889999");

		// Create a pet type
		PetType dogType = new PetType();
		dogType.setName("dog");

		// Save the pet type first to ensure it has an ID
		petTypeRepository.save(dogType);

		// Create a pet
		Pet pet = new Pet();
		pet.setName("Snoopy");
		pet.setBirthDate(LocalDate.now().minusYears(2));
		pet.setType(dogType);

		// Add pet to owner
		owner.addPet(pet);

		// Save the owner (which should cascade to the pet)
		ownerRepository.save(owner);

		// Retrieve the owner with pets
		Owner foundOwner = ownerRepository.findById(owner.getId()).orElse(null);
		assertThat(foundOwner).isNotNull();
		assertThat(foundOwner.getPets()).hasSize(1);
		assertThat(foundOwner.getPets().iterator().next().getName()).isEqualTo("Snoopy");
	}

	/**
	 * Test creating and retrieving a visit for a pet. This verifies that complex
	 * relationships work correctly in a native image.
	 */
	@Test
	void testVisitRelationship() {
		// Create an owner with a pet
		Owner owner = new Owner();
		owner.setFirstName("Lucy");
		owner.setLastName("Van Pelt");
		owner.setAddress("303 Maple St");
		owner.setCity("Cartoon");
		owner.setTelephone("0001112222");

		PetType catType = new PetType();
		catType.setName("cat");

		// Save the pet type first to ensure it has an ID
		petTypeRepository.save(catType);

		Pet pet = new Pet();
		pet.setName("Garfield");
		pet.setBirthDate(LocalDate.now().minusYears(5));
		pet.setType(catType);

		owner.addPet(pet);

		// Create a visit for the pet
		Visit visit = new Visit();
		visit.setDate(LocalDate.now());
		visit.setDescription("Annual checkup");
		pet.addVisit(visit);

		// Save the owner (which should cascade to pet and visit)
		ownerRepository.save(owner);

		// Retrieve the owner with pets and visits
		Owner foundOwner = ownerRepository.findById(owner.getId()).orElse(null);
		assertThat(foundOwner).isNotNull();
		assertThat(foundOwner.getPets()).hasSize(1);

		Pet foundPet = foundOwner.getPets().iterator().next();
		assertThat(foundPet.getName()).isEqualTo("Garfield");
		assertThat(foundPet.getVisits()).hasSize(1);
		assertThat(foundPet.getVisits().iterator().next().getDescription()).isEqualTo("Annual checkup");
	}

}
