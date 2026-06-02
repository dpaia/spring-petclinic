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

import java.util.Comparator;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.util.Assert;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class OwnerRepositoryImpl implements OwnerRepositoryCustom {

	private static final Comparator<Pet> PET_NAME_COMPARATOR = Comparator.comparing(Pet::getName,
			Comparator.nullsLast(String::compareToIgnoreCase));

	private static final Comparator<Visit> VISIT_DATE_COMPARATOR = Comparator.comparing(Visit::getDate,
			Comparator.nullsLast(Comparator.naturalOrder()));

	private final R2dbcEntityTemplate template;

	private final PetRepository pets;

	private final PetTypeRepository types;

	private final VisitRepository visits;

	public OwnerRepositoryImpl(R2dbcEntityTemplate template, PetRepository pets, PetTypeRepository types,
			VisitRepository visits) {
		this.template = template;
		this.pets = pets;
		this.types = types;
		this.visits = visits;
	}

	@Override
	public Mono<Owner> findById(Integer id) {
		Assert.notNull(id, "Owner identifier must not be null");
		return this.template.selectOne(Query.query(Criteria.where("id").is(id)), Owner.class).flatMap(this::hydrate);
	}

	@Override
	public Flux<Owner> findByLastNameStartingWith(String lastName, Pageable pageable) {
		Query query = Query.query(Criteria.where("last_name").like((lastName != null ? lastName : "") + "%"))
			.sort(Sort.by("last_name", "first_name"));
		if (pageable != null && pageable.isPaged()) {
			query = query.limit(pageable.getPageSize()).offset(pageable.getOffset());
		}
		if (pageable != null && pageable.getSort().isSorted()) {
			query = query.sort(pageable.getSort());
		}
		return this.template.select(Owner.class).matching(query).all().flatMap(this::hydrate);
	}

	@Override
	public <S extends Owner> Mono<S> save(S owner) {
		Assert.notNull(owner, "Owner must not be null");
		return persistOwner(owner).flatMap(this::savePets);
	}

	@SuppressWarnings("unchecked")
	private <S extends Owner> Mono<S> persistOwner(S owner) {
		Mono<Owner> savedOwner = owner.isNew() ? this.template.insert(Owner.class).using(owner)
				: this.template.update(owner);
		return savedOwner.map(saved -> (S) saved);
	}

	private <S extends Owner> Mono<S> savePets(S owner) {
		return Flux.fromIterable(owner.getPets()).concatMap(pet -> savePet(owner, pet)).then(Mono.just(owner));
	}

	private Mono<Pet> savePet(Owner owner, Pet pet) {
		pet.setOwnerId(owner.getId());
		if (pet.getType() != null) {
			pet.setTypeId(pet.getType().getId());
		}
		Mono<Pet> savedPet = pet.isNew() ? this.template.insert(Pet.class).using(pet) : this.template.update(pet);
		return savedPet.flatMap(petToSave -> saveVisits(petToSave).thenReturn(petToSave));
	}

	private Mono<Void> saveVisits(Pet pet) {
		return Flux.fromIterable(pet.getVisits()).concatMap(visit -> {
			visit.setPetId(pet.getId());
			return visit.isNew() ? this.template.insert(Visit.class).using(visit) : this.template.update(visit);
		}).then();
	}

	private Mono<Owner> hydrate(Owner owner) {
		return this.pets.findByOwnerId(owner.getId())
			.flatMap(this::hydrate)
			.sort(PET_NAME_COMPARATOR)
			.collectList()
			.doOnNext(pets -> {
				owner.getPets().clear();
				owner.getPets().addAll(pets);
			})
			.thenReturn(owner);
	}

	private Mono<Pet> hydrate(Pet pet) {
		Mono<Void> type = pet.getTypeId() == null ? Mono.empty()
				: this.types.findById(pet.getTypeId()).doOnNext(pet::setType).then();
		Mono<Void> visits = this.visits.findByPetId(pet.getId())
			.sort(VISIT_DATE_COMPARATOR)
			.collectList()
			.doOnNext(values -> {
				pet.getVisits().clear();
				pet.getVisits().addAll(values);
			})
			.then();
		return Mono.when(type, visits).thenReturn(pet);
	}

}
