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

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import reactor.test.StepVerifier;

import java.util.Set;
import java.util.stream.Collectors;

@DataR2dbcTest
class R2dbcVisitRepositoryTests {

	@Autowired
	private VisitRepository visitRepository;

	@Test
	void shouldFindVisitsByPetId() {
		visitRepository.findByPetId(7).collectList().as(StepVerifier::create).expectNextMatches(visits -> {
			if (visits == null)
				return false;
			Set<String> descriptions = visits.stream().map(Visit::getDescription).collect(Collectors.toSet());
			return visits.size() == 2 && descriptions.contains("rabies shot") && descriptions.contains("spayed")
					&& visits.stream().allMatch(v -> v.getPetId() == 7);
		}).verifyComplete();
	}

	@Test
	void shouldReturnEmptyWhenNoVisitsForPetId() {
		visitRepository.findByPetId(1)
			.collectList()
			.as(StepVerifier::create)
			.expectNextMatches(java.util.List::isEmpty)
			.verifyComplete();
	}

}
