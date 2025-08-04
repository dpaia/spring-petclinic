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

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Transactional(readOnly = true)
public class PetServiceImpl implements PetService {

	private final PetRepository petRepository;

	private final PetTypeRepository petTypeRepository;

	private final VisitRepository visitRepository;

	public PetServiceImpl(PetRepository petRepository, PetTypeRepository petTypeRepository,
			VisitRepository visitRepository) {
		this.petRepository = petRepository;
		this.petTypeRepository = petTypeRepository;
		this.visitRepository = visitRepository;
	}

	@Override
	public Flux<Pet> findByOwnerId(Integer ownerId) {
		return petRepository.findByOwnerId(ownerId).flatMap(this::load);
	}

	private Mono<Pet> load(Pet pet) {
		// Load pet type if typeId is available
		Mono<Pet> petWithType = Mono.just(pet);
		if (pet.getTypeId() != null) {
			petWithType = petTypeRepository.findById(pet.getTypeId()).map(petType -> {
				pet.setType(petType);
				return pet;
			}).switchIfEmpty(Mono.just(pet));
		}

		// Load visits if pet has an ID
		if (pet.getId() != null) {
			return petWithType.flatMap(p -> visitRepository.findByPetId(p.getId()).collectList().map(visits -> {
				visits.forEach(p::addVisit);
				return p;
			}).switchIfEmpty(Mono.just(p)));
		}

		return petWithType;
	}

	@Override
	public Mono<Pet> findByIdAndOwnerId(Integer id, Integer ownerId) {
		return petRepository.findByIdAndOwnerId(id, ownerId).flatMap(this::load);
	}

	@Override
	@Transactional
	public Mono<Pet> save(Owner owner, Pet pet) {
		pet.setOwnerId(owner.getId());
		return petRepository.save(pet);
	}

}
