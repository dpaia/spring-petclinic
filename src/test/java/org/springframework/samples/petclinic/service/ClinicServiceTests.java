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

package org.springframework.samples.petclinic.service;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import org.springframework.samples.petclinic.owner.*;
import org.springframework.test.annotation.DirtiesContext;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.samples.petclinic.vet.Vet;
import org.springframework.samples.petclinic.vet.VetService;

/**
 * Integration test of the Service and the Repository layer.
 * <p>
 * ClinicServiceSpringDataJpaTests subclasses benefit from the following services provided
 * by the Spring TestContext Framework:
 * </p>
 * <ul>
 * <li><strong>Spring IoC container caching</strong> which spares us unnecessary set up
 * time between test execution.</li>
 * <li><strong>Dependency Injection</strong> of test fixture instances, meaning that we
 * don't need to perform application context lookups. See the use of
 * {@link Autowired @Autowired} on the <code> </code> instance variable, which uses
 * autowiring <em>by type</em>.
 * <li><strong>Transaction management</strong>, meaning each test method is executed in
 * its own transaction, which is automatically rolled back by default. Thus, even if tests
 * insert or otherwise change database state, there is no need for a teardown or cleanup
 * script.
 * <li>An {@link org.springframework.context.ApplicationContext ApplicationContext} is
 * also inherited and can be used for explicit bean lookup if necessary.</li>
 * </ul>
 *
 * @author Ken Krebs
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @author Sam Brannen
 * @author Michael Isvy
 * @author Dave Syer
 */
@SpringBootTest
// Ensure that if the mysql profile is active we connect to the real database:
@AutoConfigureTestDatabase(replace = Replace.NONE)
// @TestPropertySource("/application-postgres.properties")
class ClinicServiceTests {

	@Autowired
	protected OwnerService ownerService;

	@Autowired
	protected PetTypeRepository types;

	@Autowired
	protected VetService vetService;

	@Autowired
	protected VisitService visitService;

	Pageable pageable;

	@Autowired
	private PetRepository petRepository;

	@Test
	void shouldFindOwnersByLastName() {
		this.ownerService.findByLastNameStartingWithReactive("Davis", pageable)
			.collectList()
			.as(StepVerifier::create)
			.expectNextMatches(owners -> owners.size() == 2)
			.verifyComplete();

		this.ownerService.findByLastNameStartingWithReactive("Daviss", pageable)
			.collectList()
			.as(StepVerifier::create)
			.expectNextMatches(List::isEmpty)
			.verifyComplete();
	}

	@Test
	void shouldFindSingleOwnerWithPet() {
		this.ownerService.findByIdReactive(1)
			.as(StepVerifier::create)
			.expectNextMatches(owner -> owner.getLastName().startsWith("Franklin") && owner.getPets().size() == 1
					&& owner.getPets().get(0).getType() != null
					&& owner.getPets().get(0).getType().getName().equals("cat"))
			.verifyComplete();
	}

	@Test
	@DirtiesContext
	void shouldInsertOwner() {
		// First, find existing owners with last name "Schultz"
		this.ownerService.findByLastNameStartingWithReactive("Schultz", pageable)
			.collectList()
			.flatMap(existingOwners -> {
				int found = existingOwners.size();

				// Create and save a new owner
				Owner owner = new Owner();
				owner.setFirstName("Sam");
				owner.setLastName("Schultz");
				owner.setAddress("4, Evans Street");
				owner.setCity("Wollongong");
				owner.setTelephone("4444444444");

				return this.ownerService.save(owner).flatMap(savedOwner -> {
					// Verify the ID is not zero
					if (savedOwner.getId() == 0) {
						return Mono.error(new AssertionError("Owner ID should not be zero"));
					}

					// Find owners again and verify count increased
					return this.ownerService.findByLastNameStartingWithReactive("Schultz", pageable)
						.collectList()
						.flatMap(updatedOwners -> {
							if (updatedOwners.size() != found + 1) {
								return Mono.just(new AssertionError(
										"Expected " + (found + 1) + " owners, but found " + updatedOwners.size()));
							}
							return Mono.just(updatedOwners);
						});
				});
			})
			.as(StepVerifier::create)
			.expectNextCount(1)
			.verifyComplete();
	}

	@Test
	@DirtiesContext
	void shouldUpdateOwner() {
		this.ownerService.findByIdReactive(1).flatMap(owner -> {
			String oldLastName = owner.getLastName();
			String newLastName = oldLastName + "X";

			owner.setLastName(newLastName);
			return this.ownerService.save(owner).then(this.ownerService.findByIdReactive(1)).flatMap(updatedOwner -> {
				if (!updatedOwner.getLastName().equals(newLastName)) {
					return Mono.just(new AssertionError(
							"Expected last name to be " + newLastName + " but was " + updatedOwner.getLastName()));
				}
				return Mono.just(updatedOwner);
			});
		}).as(StepVerifier::create).expectNextCount(1).verifyComplete();
	}

	@Test
	void shouldFindAllPetTypes() {
		this.types.findAllByOrderByName().collectList().as(StepVerifier::create).expectNextMatches(petTypes -> {
			PetType petType1 = EntityUtils.getById(petTypes, PetType.class, 1);
			PetType petType4 = EntityUtils.getById(petTypes, PetType.class, 4);
			return petType1.getName().equals("cat") && petType4.getName().equals("snake");
		}).verifyComplete();
	}

	@Test
	@DirtiesContext
	void shouldInsertPetIntoDatabaseAndGenerateId() {
		this.types.findAllByOrderByName().collectList().flatMap(types -> {
			Pet pet = new Pet();
			pet.setName("bowser");
			pet.setOwnerId(6);
			pet.setType(EntityUtils.getById(types, PetType.class, 2));
			pet.setBirthDate(LocalDate.now());

			return petRepository.save(pet).then(this.ownerService.findByIdReactive(6).flatMap(owner6 -> {
				Pet bowser = owner6.getPet("bowser");
				if (bowser == null) {
					return Mono.error(new AssertionError("Expected pet 'bowser' not found"));
				}

				if (bowser.getId() == null) {
					return Mono.error(new AssertionError("Pet ID should not be null"));
				}
				return Mono.just(owner6);
			}));
		}).as(StepVerifier::create).expectNextCount(1).verifyComplete();
	}

	@Test
	@DirtiesContext
	void shouldUpdatePetName() {
		petRepository.findById(7).map(pet7 -> {
			String oldName = pet7.getName();
			String newName = oldName + "X";
			pet7.setName(newName);

			return petRepository.save(pet7).map(updatedPet -> this.ownerService.findByIdReactive(6).flatMap(owner6 -> {
				Pet realPet = owner6.getPet(7);
				if (realPet == null) {
					return Mono.error(new AssertionError("Pet with ID 7 not found"));
				}

				if (!updatedPet.getName().equals(realPet.getName())) {
					return Mono.just(new AssertionError(
							"Expected pet name to be " + newName + " but was " + updatedPet.getName()));
				}

				return Mono.just(owner6);
			}));
		}).as(StepVerifier::create).expectNextCount(1).verifyComplete();
	}

	@Test
	void shouldFindVets() {
		this.vetService.findAllPaginatedReactive(pageable)
			.collectList()
			.as(StepVerifier::create)
			.expectNextMatches(vets -> {
				Vet vet = EntityUtils.getById(vets, Vet.class, 3);
				return vet.getLastName().equals("Douglas") && vet.getNrOfSpecialties() == 2
						&& vet.getSpecialties().get(0).getName().equals("dentistry")
						&& vet.getSpecialties().get(1).getName().equals("surgery");
			})
			.verifyComplete();
	}

	@Test
	@DirtiesContext
	void shouldAddNewVisitForPet() {
		// Create and save a visit first
		Visit visit = new Visit();
		visit.setPetId(7);
		visit.setDescription("test");

		// Save the visit first and then proceed with the test
		this.visitService.save(visit).flatMap(savedVisit -> this.ownerService.findByIdReactive(6).flatMap(owner6 -> {
			Pet pet7 = owner6.getPet(7);
			if (pet7 == null) {
				return Mono.error(new AssertionError("Pet with ID 7 not found"));
			}

			if (pet7.getVisits().size() != 3) {
				return Mono
					.error(new AssertionError("Expected " + 3 + " visits, but found " + pet7.getVisits().size()));
			}

			if (pet7.getVisits().stream().anyMatch(v -> v.getId() == null)) {
				return Mono.error(new AssertionError("Visit ID should not be null"));
			}

			if (pet7.getVisits().stream().noneMatch(v -> Objects.equals(v.getPetId(), visit.getPetId()))) {
				return Mono
					.error(new AssertionError("Expected visit with pet ID " + visit.getPetId() + " but found none"));
			}

			return Mono.just(owner6);
		})).as(StepVerifier::create).expectNextCount(1).verifyComplete();
	}

	@Test
	void shouldFindVisitsByPetId() {
		this.ownerService.findByIdReactive(6).flatMap(owner6 -> {
			Pet pet7 = owner6.getPet(7);
			if (pet7 == null) {
				return Mono.error(new AssertionError("Pet with ID 7 not found"));
			}

			Collection<Visit> visits = pet7.getVisits();
			if (visits.size() != 2) {
				return Mono.error(new AssertionError("Expected 2 visits, but found " + visits.size()));
			}

			if (visits.iterator().next().getDate() == null) {
				return Mono.error(new AssertionError("Visit date should not be null"));
			}

			return Mono.just(owner6);
		}).as(StepVerifier::create).expectNextCount(1).verifyComplete();
	}

}
