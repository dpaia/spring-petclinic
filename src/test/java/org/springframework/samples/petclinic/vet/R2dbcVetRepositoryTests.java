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

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.data.domain.PageRequest;
import reactor.test.StepVerifier;

import java.util.Set;
import java.util.stream.Collectors;

@DataR2dbcTest
class R2dbcVetRepositoryTests {

	@Autowired
	private VetRepository vetRepository;

	@Test
	void shouldFindAllVets() {
		vetRepository.findAll()
			.collectList()
			.as(StepVerifier::create)
			.expectNextMatches(vets -> vets != null && vets.size() == 6
					&& vets.stream()
						.map(Vet::getLastName)
						.collect(Collectors.toSet())
						.containsAll(Set.of("Carter", "Leary", "Douglas", "Ortega", "Stevens", "Jenkins")))
			.verifyComplete();
	}

	@Test
	void shouldFindAllVetsWithPagination() {
		vetRepository.findAllBy(PageRequest.of(0, 3))
			.collectList()
			.as(StepVerifier::create)
			.expectNextMatches(page -> page != null && page.size() == 3)
			.verifyComplete();
	}

}
