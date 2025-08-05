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

import jakarta.annotation.Nullable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ServerWebExchange;

import jakarta.validation.Valid;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author Juergen Hoeller
 * @author Ken Krebs
 * @author Arjen Poutsma
 * @author Michael Isvy
 * @author Wick Dynex
 */
@Controller
class OwnerController {

	private static final String VIEWS_OWNER_CREATE_OR_UPDATE_FORM = "owners/createOrUpdateOwnerForm";

	private static final int PAGE_SIZE = 5;

	private final OwnerService ownerService;

	public OwnerController(OwnerService ownerService) {
		this.ownerService = ownerService;
	}

	@InitBinder
	public void setAllowedFields(WebDataBinder dataBinder) {
		dataBinder.setDisallowedFields("id");
	}

	@ModelAttribute("owner")
	public Mono<Owner> findOwner(@PathVariable(name = "ownerId", required = false) Integer ownerId) {
		if (ownerId == null) {
			return Mono.just(new Owner());
		}

		return ownerService.findByIdReactive(ownerId);
	}

	@GetMapping("/owners/new")
	public Mono<String> initCreationForm() {
		return Mono.just(VIEWS_OWNER_CREATE_OR_UPDATE_FORM);
	}

	@PostMapping("/owners/new")
	public Mono<String> processCreationForm(@Valid Owner owner, BindingResult result, ServerWebExchange exchange) {
		if (result.hasErrors()) {
			return exchange.getSession()
				.doOnNext(session -> session.getAttributes().put("error", "There was an error in creating the owner."))
				.then(Mono.just(VIEWS_OWNER_CREATE_OR_UPDATE_FORM));
		}

		return ownerService.save(owner)
			.flatMap(savedOwner -> exchange.getSession()
				.doOnNext(session -> session.getAttributes().put("message", "New Owner Created"))
				.then(Mono.just("redirect:/owners/" + savedOwner.getId())));
	}

	@GetMapping("/owners/find")
	public Mono<String> initFindForm() {
		return Mono.just("owners/findOwners");
	}

	@GetMapping("/owners")
	public Mono<String> processFindForm(@RequestParam(defaultValue = "1") int page, Owner owner, BindingResult result,
			Model model) {
		Mono<List<Owner>> owners = findPaginatedForOwnersLastNameReactive(page, owner.getLastName()).collectList();

		return owners.map(ownersResults -> {
			if (ownersResults.isEmpty()) {
				result.rejectValue("lastName", "notFound", "not found");
				return "owners/findOwners";
			}

			if (ownersResults.size() == 1) {
				Owner foundOwner = ownersResults.get(0);
				return "redirect:/owners/" + foundOwner.getId();
			}

			return addPaginationModel(page, model, ownersResults);
		});
	}

	private String addPaginationModel(int page, Model model, List<Owner> listOwners) {
		model.addAttribute("currentPage", page);
		// model.addAttribute("totalPages", paginated.getTotalPages());
		// model.addAttribute("totalItems", paginated.getTotalElements());
		model.addAttribute("listOwners", listOwners);
		return "owners/ownersList";
	}

	private Flux<Owner> findPaginatedForOwnersLastNameReactive(int page, @Nullable String lastname) {
		Pageable pageable = PageRequest.of(page - 1, PAGE_SIZE);
		return ownerService.findByLastNameStartingWithReactive(lastname, pageable);
	}

	@GetMapping("/owners/{ownerId}/edit")
	public Mono<String> initUpdateOwnerForm() {
		return Mono.just(VIEWS_OWNER_CREATE_OR_UPDATE_FORM);
	}

	@PostMapping("/owners/{ownerId}/edit")
	public Mono<String> processUpdateOwnerForm(@Valid Owner owner, BindingResult result,
			@PathVariable("ownerId") int ownerId, ServerWebExchange exchange) {
		if (result.hasErrors()) {
			return exchange.getSession()
				.doOnNext(session -> session.getAttributes().put("error", "There was an error in updating the owner."))
				.then(Mono.just(VIEWS_OWNER_CREATE_OR_UPDATE_FORM));
		}

		if (owner.getId() != ownerId) {
			result.rejectValue("id", "mismatch", "The owner ID in the form does not match the URL.");
			return exchange.getSession()
				.doOnNext(session -> session.getAttributes().put("error", "Owner ID mismatch. Please try again."))
				.then(Mono.just("redirect:/owners/{ownerId}/edit"));
		}

		return ownerService.save(owner)
			.flatMap(savedOwner -> exchange.getSession()
				.doOnNext(session -> session.getAttributes().put("message", "Owner Values Updated"))
				.then(Mono.just("redirect:/owners/{ownerId}")));
	}

	/**
	 * Custom handler for displaying an owner.
	 * @param ownerId the ID of the owner to display
	 * @return a Mono<String> with the view name and model attributes
	 */
	@GetMapping("/owners/{ownerId}")
	public Mono<String> showOwner(@PathVariable("ownerId") int ownerId, Model model, ServerWebExchange exchange) {
		return exchange.getSession().flatMap(session -> {
			Object message = session.getAttribute("message");
			if (message != null) {
				model.addAttribute("message", message);
				session.getAttributes().remove("message");
			}

			return ownerService.findByIdReactive(ownerId)
				.doOnNext(owner -> model.addAttribute("owner", owner))
				.map(owner -> "owners/ownerDetails");
		});
	}

}
