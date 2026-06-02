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

import io.r2dbc.spi.Row;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Query;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class VetRepositoryImpl implements VetRepositoryCustom {

	private final R2dbcEntityTemplate template;

	private final DatabaseClient databaseClient;

	public VetRepositoryImpl(R2dbcEntityTemplate template, DatabaseClient databaseClient) {
		this.template = template;
		this.databaseClient = databaseClient;
	}

	@Override
	public Flux<Vet> findAll() {
		return this.template.select(Vet.class).matching(Query.empty().sort(Sort.by("id"))).all().flatMap(this::hydrate);
	}

	@Override
	public Flux<Vet> findAllBy(Pageable pageable) {
		Query query = Query.empty().sort(Sort.by("id"));
		if (pageable != null && pageable.isPaged()) {
			query = query.limit(pageable.getPageSize()).offset(pageable.getOffset());
		}
		if (pageable != null && pageable.getSort().isSorted()) {
			query = query.sort(pageable.getSort());
		}
		return this.template.select(Vet.class).matching(query).all().flatMap(this::hydrate);
	}

	private Mono<Vet> hydrate(Vet vet) {
		vet.getSpecialtiesInternal().clear();
		return this.databaseClient.sql("""
				SELECT s.id, s.name
				FROM specialties s
				INNER JOIN vet_specialties vs ON vs.specialty_id = s.id
				WHERE vs.vet_id = :vetId
				""")
			.bind("vetId", vet.getId())
			.map((row, rowMetadata) -> specialty(row))
			.all()
			.doOnNext(vet::addSpecialty)
			.then(Mono.just(vet));
	}

	private Specialty specialty(Row row) {
		Specialty specialty = new Specialty();
		Number id = row.get("id", Number.class);
		specialty.setId(id != null ? id.intValue() : null);
		specialty.setName(row.get("name", String.class));
		return specialty;
	}

}
