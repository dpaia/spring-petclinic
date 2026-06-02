/*
 * Copyright 2012-2019 the original author or authors.
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
import org.springframework.data.domain.AuditorAware;
import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.samples.petclinic.owner.OwnerRepository;
import org.springframework.samples.petclinic.owner.Pet;
import org.springframework.samples.petclinic.owner.PetType;
import org.springframework.samples.petclinic.owner.Visit;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for the auditing functionality.
 */
@SpringBootTest
class AuditingTests {

	@Autowired
	private AuditorAware<String> auditorAware;

	@Autowired
	private OwnerRepository ownerRepository;

	@Test
	void testAuditorAware() {
		Optional<String> auditor = auditorAware.getCurrentAuditor();
		assertThat(auditor).isPresent();
		assertThat(auditor.get()).isEqualTo("system");
	}

	@Test
	@Transactional
	void testAuditFieldsOnCreate() {
		Owner owner = new Owner();
		owner.setFirstName("John");
		owner.setLastName("Doe");
		owner.setAddress("123 Main St");
		owner.setCity("New York");
		owner.setTelephone("1234567890");

		ownerRepository.save(owner);

		assertThat(owner.getCreatedDate()).isNotNull();
		assertThat(owner.getLastModifiedDate()).isNotNull();
		assertThat(owner.getCreatedBy()).isEqualTo("system");
		assertThat(owner.getLastModifiedBy()).isEqualTo("system");
	}

	@Test
	// @Transactional
	void testAuditFieldsOnUpdate() {
		// Create a new owner
		Owner owner = new Owner();
		owner.setFirstName("Jane");
		owner.setLastName("Smith");
		owner.setAddress("456 Oak St");
		owner.setCity("Boston");
		owner.setTelephone("0987654321");

		owner = ownerRepository.save(owner);

		// Capture initial audit values
		String initialCreatedBy = owner.getCreatedBy();
		var initialCreatedDate = owner.getCreatedDate();
		var initialLastModifiedDate = owner.getLastModifiedDate();

		// Wait a moment to ensure timestamps will be different
		try {
			Thread.sleep(10);
		}
		catch (InterruptedException e) {
			System.out.println();
			// Ignore
		}

		// Update the owner
		owner.setAddress("789 Pine St");
		owner = ownerRepository.save(owner);

		// Verify audit fields
		assertThat(owner.getCreatedBy()).isEqualTo(initialCreatedBy);
		assertThat(owner.getCreatedDate()).isEqualTo(initialCreatedDate);
		assertThat(owner.getLastModifiedBy()).isEqualTo("system");
		assertThat(owner.getLastModifiedDate()).isAfter(initialLastModifiedDate);
	}

	@Test
	@Transactional
	void testAuditingForPetAndVisit() {
		// Create owner and pet
		Owner owner = new Owner();
		owner.setFirstName("John");
		owner.setLastName("Doe");
		owner.setAddress("123 Main St");
		owner.setCity("New York");
		owner.setTelephone("1234567890");

		Pet pet = new Pet();
		pet.setName("Fluffy");
		pet.setBirthDate(LocalDate.now().minusYears(2));

		PetType petType = ownerRepository.findPetTypeByName("cat");
		pet.setType(petType);

		owner.addPet(pet);

		ownerRepository.save(owner);

		// Verify pet audit fields
		assertThat(pet.getCreatedDate()).isNotNull();
		assertThat(pet.getLastModifiedDate()).isNotNull();
		assertThat(pet.getCreatedBy()).isEqualTo("system");
		assertThat(pet.getLastModifiedBy()).isEqualTo("system");

		// Create visit
		Visit visit = new Visit();
		visit.setDate(LocalDate.now());
		visit.setDescription("Annual checkup");
		pet.addVisit(visit);

		owner = ownerRepository.save(owner);
		visit = owner.getPet(pet.getName()).getVisits().iterator().next();

		// Verify visit audit fields
		assertThat(visit.getCreatedDate()).isNotNull();
		assertThat(visit.getLastModifiedDate()).isNotNull();
		assertThat(visit.getCreatedBy()).isEqualTo("system");
		assertThat(visit.getLastModifiedBy()).isEqualTo("system");
	}

}
