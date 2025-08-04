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
package org.springframework.samples.petclinic.vet;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class VetServiceImpl implements VetService {

	private final VetRepository vetRepository;

	private final VetSpecialtyRepository vetSpecialtyRepository;

	private final SpecialtyRepository specialtyRepository;

	public VetServiceImpl(VetRepository vetRepository, VetSpecialtyRepository vetSpecialtyRepository,
			SpecialtyRepository specialtyRepository) {
		this.vetRepository = vetRepository;
		this.vetSpecialtyRepository = vetSpecialtyRepository;
		this.specialtyRepository = specialtyRepository;
	}

	@Override
	public Mono<Vets> getVetsAsListReactive() {
		return vetRepository.findAll().flatMap(this::load).collectList().map(vetList -> {
			Vets vets = new Vets();
			vets.getVetList().addAll(vetList);
			return vets;
		});
	}

	@Override
	public Flux<Vet> findAllPaginatedReactive(Pageable pageable) {
		return vetRepository.findAllBy(pageable).flatMap(this::load);
	}

	private Mono<Vet> load(Vet vet) {
		if (!vet.getSpecialties().isEmpty())
			return Mono.just(vet);

		Mono<Map<Integer, Specialty>> specialtyMapMono = specialtyRepository.findAll().collectMap(Specialty::getId);

		return vetSpecialtyRepository.findAllByVetId(vet.getId()).collectList().zipWith(specialtyMapMono).map(tuple -> {
			List<VetSpecialty> vetSpecialties = tuple.getT1();
			Map<Integer, Specialty> specialtyMap = tuple.getT2();

			// Add all specialties to the vet
			for (VetSpecialty vetSpecialty : vetSpecialties) {
				Specialty specialty = specialtyMap.get(vetSpecialty.getSpecialtyId());
				if (specialty != null) {
					vet.addSpecialty(specialty);
				}
			}

			return vet;
		});
	}

}
