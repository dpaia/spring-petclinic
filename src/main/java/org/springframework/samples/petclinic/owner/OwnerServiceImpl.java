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

import jakarta.annotation.Nullable;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class OwnerServiceImpl implements OwnerService {

	private final OwnerRepository ownerRepository;

	private final PetService petService;

	public OwnerServiceImpl(OwnerRepository ownerRepository, PetService petService) {
		this.ownerRepository = ownerRepository;
		this.petService = petService;
	}

	@Override
	public Mono<Tuple2<List<Owner>, Long>> findByLastNameStartingWithReactive(@Nullable String lastName,
			Pageable pageable) {
		Flux<Owner> owners = ownerRepository.findByLastNameStartingWith(lastName == null ? "" : lastName, pageable)
			.flatMap(this::load);
		return owners.collectList().zipWith(ownerRepository.count());
	}

	@Override
	@Transactional
	public Mono<Owner> save(Owner owner) {
		return ownerRepository.save(owner);
	}

	@Override
	public Mono<Owner> findByIdReactive(Integer id) {
		return ownerRepository.findById(id).flatMap(this::load);
	}

	private Flux<Pet> findPetsByOwnerIdReactive(Integer ownerId) {
		return petService.findByOwnerId(ownerId);
	}

	private Mono<Owner> load(Owner owner) {
		return findPetsByOwnerIdReactive(owner.getId()).doOnNext(owner::addPet).then(Mono.just(owner));
	}

}
