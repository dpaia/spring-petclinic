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
 * REST controller for accessing visit data
 *
 * @author Junie
 */
@RestController
@RequestMapping("/api")
public class VisitRestController {

	private final VisitRepository visitRepository;

	private final PetRepository petRepository;

	public VisitRestController(VisitRepository visitRepository, PetRepository petRepository) {
		this.visitRepository = visitRepository;
		this.petRepository = petRepository;
	}

	/**
	 * Get all visits
	 * @return a list of all visits
	 */
	@GetMapping("/visits")
	public ResponseEntity<List<Visit>> getAllVisits() {
		List<Visit> visits = this.visitRepository.findAll();
		return ResponseEntity.ok(visits);
	}

	/**
	 * Get a specific visit by ID
	 * @param visitId the visit ID
	 * @return the visit with the given ID
	 */
	@GetMapping("/visits/{visitId}")
	public ResponseEntity<Visit> getVisit(@PathVariable("visitId") int visitId) {
		Optional<Visit> visit = this.visitRepository.findById(visitId);
		return visit.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
	}

	/**
	 * Get all visits for a specific pet
	 * @param petId the pet ID
	 * @return a list of visits for the given pet
	 */
	@GetMapping("/pets/{petId}/visits")
	public ResponseEntity<List<Visit>> getVisitsByPet(@PathVariable("petId") int petId) {
		// Check if pet exists
		if (!this.petRepository.existsById(petId)) {
			return ResponseEntity.notFound().build();
		}

		List<Visit> visits = this.visitRepository.findByPetId(petId);
		return ResponseEntity.ok(visits);
	}

	/**
	 * Add a new visit to a pet
	 * @param petId the pet ID
	 * @param visit the visit to add
	 * @return the created visit
	 */
	@PostMapping("/pets/{petId}/visits")
	public ResponseEntity<Object> addVisit(@PathVariable("petId") int petId, @Valid @RequestBody Visit visit) {
		// Check if pet exists
		Optional<Pet> optionalPet = this.petRepository.findById(petId);
		if (optionalPet.isEmpty()) {
			return ResponseEntity.notFound().build();
		}

		Pet pet = optionalPet.get();

		// Validate visit
		if (visit.getDescription() == null || visit.getDescription().trim().isEmpty()) {
			return ResponseEntity.badRequest().body("Visit description is required");
		}

		// Add visit to pet
		pet.addVisit(visit);
		this.petRepository.save(pet);

		// Create URI for the new resource
		URI location = ServletUriComponentsBuilder.fromCurrentRequest()
			.path("/{id}")
			.buildAndExpand(visit.getId())
			.toUri();

		return ResponseEntity.created(location).body(visit);
	}

	/**
	 * Update an existing visit
	 * @param visitId the visit ID
	 * @param visitDetails the updated visit details
	 * @return the updated visit
	 */
	@PutMapping("/visits/{visitId}")
	public ResponseEntity<Object> updateVisit(@PathVariable("visitId") int visitId,
			@Valid @RequestBody Visit visitDetails) {
		Optional<Visit> optionalVisit = this.visitRepository.findById(visitId);
		if (optionalVisit.isEmpty()) {
			return ResponseEntity.notFound().build();
		}

		Visit visit = optionalVisit.get();

		// Update visit
		visit.setDate(visitDetails.getDate());
		visit.setDescription(visitDetails.getDescription());

		Visit updatedVisit = this.visitRepository.save(visit);
		return ResponseEntity.ok(updatedVisit);
	}

	/**
	 * Delete a visit
	 * @param visitId the visit ID
	 * @return no content if successful
	 */
	@DeleteMapping("/visits/{visitId}")
	public ResponseEntity<Void> deleteVisit(@PathVariable("visitId") int visitId) {
		if (!this.visitRepository.existsById(visitId)) {
			return ResponseEntity.notFound().build();
		}

		this.visitRepository.deleteById(visitId);
		return ResponseEntity.noContent().build();
	}

}
