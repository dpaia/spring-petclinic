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

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.server.ServerWebExchange;

import jakarta.validation.Valid;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author Juergen Hoeller
 * @author Ken Krebs
 * @author Arjen Poutsma
 * @author Michael Isvy
 * @author Dave Syer
 * @author Wick Dynex
 */
@Controller
class VisitController {

	private final OwnerService ownerService;

	private final PetService petService;

	private final VisitService visitService;

	public VisitController(OwnerService ownerService, PetService petService, VisitService visitService) {
		this.ownerService = ownerService;
		this.petService = petService;
		this.visitService = visitService;
	}

	@InitBinder
	public void setAllowedFields(WebDataBinder dataBinder) {
		dataBinder.setDisallowedFields("id");
	}

	/**
	 * Called before each and every @RequestMapping annotated method. 2 goals: - Make sure
	 * we always have fresh data - Since we do not use the session scope, make sure that
	 * Pet object always has an id (Even though id is not part of the form fields)
	 * @param petId
	 * @return Mono<Visit>
	 */
	@ModelAttribute("visit")
	public Mono<Visit> loadPetWithVisit(@PathVariable("ownerId") int ownerId, @PathVariable("petId") int petId,
			Map<String, Object> model) {

		Mono<Owner> ownerMono = ownerService.findByIdReactive(ownerId)
			.switchIfEmpty(Mono.error(new IllegalArgumentException(
					"Owner not found with id: " + ownerId + ". Please ensure the ID is correct ")));

		Mono<Pet> petMono = petService.findByIdAndOwnerId(petId, ownerId)
			.switchIfEmpty(Mono.error(new IllegalArgumentException("Pet not found with id: " + petId
					+ ". Please ensure the ID is correct " + "and the pet exists in the database.")));

		return Mono.zip(ownerMono, petMono).flatMap(tuple -> {
			Owner owner = tuple.getT1();
			Pet pet = tuple.getT2();
			List<Pet> petsForOwner = owner.getPets();

			model.put("pet", pet);
			model.put("owner", owner);
			model.put("pets", petsForOwner);

			return Mono.just(new Visit());
		});
	}

	// Spring WebFlux calls method loadPetWithVisit(...) before initNewVisitForm is called
	@GetMapping("/owners/{ownerId}/pets/{petId}/visits/new")
	public Mono<String> initNewVisitForm(@ModelAttribute("visit") Visit visit) {
		return Mono.just("pets/createOrUpdateVisitForm");
	}

	// Spring WebFlux calls method loadPetWithVisit(...) before processNewVisitForm is
	// called
	@PostMapping("/owners/{ownerId}/pets/{petId}/visits/new")
	public Mono<String> processNewVisitForm(@PathVariable int petId, @Valid Visit visit, BindingResult result,
			ServerWebExchange exchange) {
		if (result.hasErrors()) {
			return Mono.just("pets/createOrUpdateVisitForm");
		}

		// Set the pet ID directly on the visit
		visit.setPetId(petId);

		return visitService.save(visit)
			.flatMap(savedVisit -> exchange.getSession()
				.doOnNext(session -> session.getAttributes().put("message", "Your visit has been booked"))
				.then(Mono.just("redirect:/owners/{ownerId}")));
	}

}
