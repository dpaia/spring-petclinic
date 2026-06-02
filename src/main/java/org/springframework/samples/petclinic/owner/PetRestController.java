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

import java.net.URI;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jakarta.validation.Valid;

/**
 * REST controller for accessing pet data
 *
 * @author Junie
 */
@RestController
@RequestMapping("/api")
public class PetRestController {

	private final PetRepository petRepository;

	private final OwnerRepository ownerRepository;

	private final PetTypeRepository petTypeRepository;

	public PetRestController(PetRepository petRepository, OwnerRepository ownerRepository,
			PetTypeRepository petTypeRepository) {
		this.petRepository = petRepository;
		this.ownerRepository = ownerRepository;
		this.petTypeRepository = petTypeRepository;
	}

	/**
	 * Get all pets
	 * @return a list of all pets
	 */
	@GetMapping("/pets")
	public ResponseEntity<List<Pet>> getAllPets() {
		List<Pet> pets = this.petRepository.findAll();
		return ResponseEntity.ok(pets);
	}

	/**
	 * Get a specific pet by ID
	 * @param petId the pet ID
	 * @return the pet with the given ID
	 */
	@GetMapping("/pets/{petId}")
	public ResponseEntity<Pet> getPet(@PathVariable("petId") int petId) {
		Optional<Pet> pet = this.petRepository.findById(petId);
		return pet.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
	}

	/**
	 * Get all pets for a specific owner
	 * @param ownerId the owner ID
	 * @return a list of pets for the given owner
	 */
	@GetMapping("/owners/{ownerId}/pets")
	public ResponseEntity<List<Pet>> getPetsByOwner(@PathVariable("ownerId") int ownerId) {
		// Check if owner exists
		if (!this.ownerRepository.existsById(ownerId)) {
			return ResponseEntity.notFound().build();
		}

		List<Pet> pets = this.petRepository.findByOwnerId(ownerId);
		return ResponseEntity.ok(pets);
	}

	/**
	 * Add a new pet to an owner
	 * @param ownerId the owner ID
	 * @param pet the pet to add
	 * @return the created pet
	 */
	@PostMapping("/owners/{ownerId}/pets")
	public ResponseEntity<Object> addPet(@PathVariable("ownerId") int ownerId, @Valid @RequestBody Pet pet) {
		// Check if owner exists
		Optional<Owner> optionalOwner = this.ownerRepository.findById(ownerId);
		if (optionalOwner.isEmpty()) {
			return ResponseEntity.notFound().build();
		}

		Owner owner = optionalOwner.get();

		// Validate pet
		if (pet.getName() == null || pet.getName().trim().isEmpty()) {
			return ResponseEntity.badRequest().body("Pet name is required");
		}

		// Check for duplicate pet name
		if (owner.getPet(pet.getName(), true) != null) {
			return ResponseEntity.status(HttpStatus.CONFLICT)
				.body("Pet with name '" + pet.getName() + "' already exists for this owner");
		}

		// Add pet to owner
		owner.addPet(pet);
		this.ownerRepository.save(owner);

		// Create URI for the new resource
		URI location = ServletUriComponentsBuilder.fromCurrentRequest()
			.path("/{id}")
			.buildAndExpand(pet.getId())
			.toUri();

		return ResponseEntity.created(location).body(pet);
	}

	/**
	 * Update an existing pet
	 * @param petId the pet ID
	 * @param petDetails the updated pet details
	 * @return the updated pet
	 */
	@PutMapping("/pets/{petId}")
	public ResponseEntity<Object> updatePet(@PathVariable("petId") int petId, @Valid @RequestBody Pet petDetails) {
		Optional<Pet> optionalPet = this.petRepository.findById(petId);
		if (optionalPet.isEmpty()) {
			return ResponseEntity.notFound().build();
		}

		Pet pet = optionalPet.get();

		// Find the owner of this pet
		List<Owner> owners = this.ownerRepository.findAll();
		Owner petOwner = null;
		for (Owner owner : owners) {
			if (owner.getPet(petId) != null) {
				petOwner = owner;
				break;
			}
		}

		if (petOwner == null) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Could not find owner for pet");
		}

		// Check for duplicate pet name if name is being changed
		if (!pet.getName().equals(petDetails.getName()) && petOwner.getPet(petDetails.getName(), false) != null) {
			return ResponseEntity.status(HttpStatus.CONFLICT)
				.body("Pet with name '" + petDetails.getName() + "' already exists for this owner");
		}

		// Update pet
		pet.setName(petDetails.getName());
		pet.setBirthDate(petDetails.getBirthDate());

		// Update pet type if provided
		if (petDetails.getType() != null && petDetails.getType().getId() != null) {
			Optional<PetType> petType = this.petTypeRepository.findById(petDetails.getType().getId());
			petType.ifPresent(pet::setType);
		}

		Pet updatedPet = this.petRepository.save(pet);
		return ResponseEntity.ok(updatedPet);
	}

	/**
	 * Delete a pet
	 * @param petId the pet ID
	 * @return no content if successful
	 */
	@DeleteMapping("/pets/{petId}")
	public ResponseEntity<Void> deletePet(@PathVariable("petId") int petId) {
		if (!this.petRepository.existsById(petId)) {
			return ResponseEntity.notFound().build();
		}

		// Find the owner of this pet
		List<Owner> owners = this.ownerRepository.findAll();
		for (Owner owner : owners) {
			Pet pet = owner.getPet(petId);
			if (pet != null) {
				// Remove pet from owner's collection
				owner.getPets().remove(pet);
				this.ownerRepository.save(owner);
				break;
			}
		}

		this.petRepository.deleteById(petId);
		return ResponseEntity.noContent().build();
	}

}
