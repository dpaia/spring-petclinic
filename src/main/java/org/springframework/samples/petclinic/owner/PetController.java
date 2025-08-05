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

import java.time.LocalDate;

import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ServerWebExchange;

import jakarta.validation.Valid;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author Juergen Hoeller
 * @author Ken Krebs
 * @author Arjen Poutsma
 * @author Wick Dynex
 */
@Controller
@RequestMapping("/owners/{ownerId}")
class PetController {

	private static final String VIEWS_PETS_CREATE_OR_UPDATE_FORM = "pets/createOrUpdatePetForm";

	private final OwnerService ownerService;

	private final PetService petService;

	private final PetTypeRepository types;

	public PetController(OwnerService ownerService, PetTypeRepository types, PetService petService) {
		this.ownerService = ownerService;
		this.types = types;
		this.petService = petService;
	}

	@ModelAttribute("types")
	public Flux<PetType> populatePetTypes() {
		return this.types.findAllByOrderByName();
	}

	@ModelAttribute("owner")
	public Mono<Owner> findOwner(@PathVariable("ownerId") int ownerId) {
		return this.ownerService.findByIdReactive(ownerId)
			.switchIfEmpty(Mono.error(new IllegalArgumentException(
					"Owner not found with id: " + ownerId + ". Please ensure the ID is correct ")));
	}

	@ModelAttribute("pet")
	public Mono<Pet> findPet(@PathVariable("ownerId") int ownerId,
			@PathVariable(name = "petId", required = false) Integer petId) {

		if (petId == null) {
			return Mono.just(new Pet());
		}

		return this.petService.findByIdAndOwnerId(petId, ownerId)
			.switchIfEmpty(Mono.error(new IllegalArgumentException("Pet not found with id: " + petId
					+ ". Please ensure the ID is correct " + "and the pet exists in the database.")));
	}

	@InitBinder("owner")
	public void initOwnerBinder(WebDataBinder dataBinder) {
		dataBinder.setDisallowedFields("id");
	}

	@InitBinder("pet")
	public void initPetBinder(WebDataBinder dataBinder) {
		dataBinder.setValidator(new PetValidator());
	}

	@GetMapping("/pets/new")
	public Mono<String> initCreationForm(Mono<Owner> owner) {
		return Mono.just(VIEWS_PETS_CREATE_OR_UPDATE_FORM);
	}

	@PostMapping("/pets/new")
	public Mono<String> processCreationForm(@ModelAttribute("owner") Mono<Owner> owner, @Valid Pet pet,
			BindingResult result, ServerWebExchange exchange) {

		LocalDate currentDate = LocalDate.now();
		if (pet.getBirthDate() != null && pet.getBirthDate().isAfter(currentDate)) {
			result.rejectValue("birthDate", "typeMismatch.birthDate");
		}

		if (result.hasErrors()) {
			return Mono.just(VIEWS_PETS_CREATE_OR_UPDATE_FORM);
		}

		return owner.flatMap(o -> {
			if (StringUtils.hasText(pet.getName()) && pet.isNew() && o.getPet(pet.getName(), true) != null) {
				result.rejectValue("name", "duplicate", "already exists");
				return Mono.just(VIEWS_PETS_CREATE_OR_UPDATE_FORM);
			}

			return this.petService.save(o, pet)
				.flatMap(savedPet -> exchange.getSession()
					.doOnNext(session -> session.getAttributes().put("message", "New Pet has been Added"))
					.then(Mono.just("redirect:/owners/{ownerId}")));
		});
	}

	@GetMapping("/pets/{petId}/edit")
	public Mono<String> initUpdateForm() {
		return Mono.just(VIEWS_PETS_CREATE_OR_UPDATE_FORM);
	}

	@PostMapping("/pets/{petId}/edit")
	public Mono<String> processUpdateForm(@ModelAttribute("owner") Mono<Owner> owner, @Valid Pet pet,
			BindingResult result, ServerWebExchange exchange) {

		String petName = pet.getName();
		LocalDate currentDate = LocalDate.now();

		if (pet.getBirthDate() != null && pet.getBirthDate().isAfter(currentDate)) {
			result.rejectValue("birthDate", "typeMismatch.birthDate");
		}

		if (result.hasErrors()) {
			return Mono.just(VIEWS_PETS_CREATE_OR_UPDATE_FORM);
		}

		return owner.flatMap(o -> {
			// Check if pet name already exists for another pet
			if (StringUtils.hasText(petName)) {
				Pet existingPet = o.getPet(petName, false);
				if (existingPet != null && !existingPet.getId().equals(pet.getId())) {
					result.rejectValue("name", "duplicate", "already exists");
					return Mono.just(VIEWS_PETS_CREATE_OR_UPDATE_FORM);
				}
			}

			return this.petService.save(o, pet)
				.flatMap(updatedPet -> exchange.getSession()
					.doOnNext(session -> session.getAttributes().put("message", "Pet details has been edited"))
					.then(Mono.just("redirect:/owners/{ownerId}")));
		});
	}

}
