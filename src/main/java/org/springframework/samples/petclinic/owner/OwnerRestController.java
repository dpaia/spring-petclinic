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
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jakarta.validation.Valid;

/**
 * REST controller for accessing owner data
 *
 * @author Junie
 */
@RestController
@RequestMapping("/api/v1/owners")
public class OwnerRestController {

	private final OwnerRepository ownerRepository;

	public OwnerRestController(OwnerRepository ownerRepository) {
		this.ownerRepository = ownerRepository;
	}

	/**
	 * Get all owners or search by last name
	 * @param lastName the last name to search for (optional)
	 * @param page the page number (1-based, defaults to 1)
	 * @param size the page size (defaults to 10)
	 * @return a list of owners
	 */
	@GetMapping
	public ResponseEntity<Page<Owner>> getOwners(@RequestParam(required = false) String lastName,
			@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size) {

		Pageable pageable = PageRequest.of(page - 1, size);
		Page<Owner> owners;

		if (lastName != null && !lastName.isEmpty()) {
			owners = this.ownerRepository.findByLastNameStartingWith(lastName, pageable);
		}
		else {
			owners = this.ownerRepository.findAll(pageable);
		}

		return ResponseEntity.ok(owners);
	}

	/**
	 * Get a specific owner by ID
	 * @param ownerId the owner ID
	 * @return the owner with the given ID
	 */
	@GetMapping("/{ownerId}")
	public ResponseEntity<Owner> getOwner(@PathVariable("ownerId") int ownerId) {
		Optional<Owner> owner = this.ownerRepository.findById(ownerId);
		return owner.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
	}

	/**
	 * Create a new owner
	 * @param owner the owner to create
	 * @return the created owner
	 */
	@PostMapping
	public ResponseEntity<Object> createOwner(@Valid @RequestBody Owner owner) {
		if (owner.getId() != null) {
			return ResponseEntity.badRequest().body("New owner cannot have an ID");
		}

		Owner savedOwner = this.ownerRepository.save(owner);

		URI location = ServletUriComponentsBuilder.fromCurrentRequest()
			.path("/{id}")
			.buildAndExpand(savedOwner.getId())
			.toUri();

		return ResponseEntity.created(location).body(savedOwner);
	}

	/**
	 * Update an existing owner
	 * @param ownerId the owner ID
	 * @param ownerDetails the updated owner details
	 * @return the updated owner
	 */
	@PutMapping("/{ownerId}")
	public ResponseEntity<Object> updateOwner(@PathVariable("ownerId") int ownerId,
			@Valid @RequestBody Owner ownerDetails) {
		Optional<Owner> optionalOwner = this.ownerRepository.findById(ownerId);
		if (optionalOwner.isEmpty()) {
			return ResponseEntity.notFound().build();
		}

		Owner owner = optionalOwner.get();

		// Ensure ID matches path variable
		if (ownerDetails.getId() != null && ownerDetails.getId() != ownerId) {
			return ResponseEntity.badRequest().body("Owner ID in body must match path variable");
		}

		// Update owner fields
		owner.setFirstName(ownerDetails.getFirstName());
		owner.setLastName(ownerDetails.getLastName());
		owner.setAddress(ownerDetails.getAddress());
		owner.setCity(ownerDetails.getCity());
		owner.setTelephone(ownerDetails.getTelephone());

		Owner updatedOwner = this.ownerRepository.save(owner);
		return ResponseEntity.ok(updatedOwner);
	}

	/**
	 * Delete an owner
	 * @param ownerId the owner ID
	 * @return no content if successful
	 */
	@DeleteMapping("/{ownerId}")
	public ResponseEntity<Void> deleteOwner(@PathVariable("ownerId") int ownerId) {
		if (!this.ownerRepository.existsById(ownerId)) {
			return ResponseEntity.notFound().build();
		}

		this.ownerRepository.deleteById(ownerId);
		return ResponseEntity.noContent().build();
	}

}
