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

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repository class for <code>Visit</code> domain objects
 *
 * @author Junie
 */
@Repository
public interface VisitRepository extends JpaRepository<Visit, Integer> {

	/**
	 * Find all visits for a given pet
	 * @param petId the pet ID
	 * @return a list of visits
	 */
	@Query("SELECT visit FROM Visit visit WHERE visit.id IN (SELECT v.id FROM Pet pet JOIN pet.visits v WHERE pet.id = :petId)")
	List<Visit> findByPetId(@Param("petId") Integer petId);

}
