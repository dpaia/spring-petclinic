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

package org.springframework.samples.petclinic;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.migration.JavaMigration;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.samples.petclinic.model.BaseEntity;
import org.springframework.samples.petclinic.model.NamedEntity;
import org.springframework.samples.petclinic.model.Person;
import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.samples.petclinic.owner.Pet;
import org.springframework.samples.petclinic.owner.PetType;
import org.springframework.samples.petclinic.owner.Visit;
import org.springframework.samples.petclinic.vet.Specialty;
import org.springframework.samples.petclinic.vet.Vet;
import org.springframework.samples.petclinic.owner.OwnerRepository;
import org.springframework.samples.petclinic.owner.PetTypeRepository;
import org.springframework.samples.petclinic.vet.VetRepository;

/**
 * RuntimeHintsRegistrar for Spring PetClinic application. Configures reflection hints for
 * JPA entities and repositories to work with GraalVM native images.
 */
public class PetClinicRuntimeHints implements RuntimeHintsRegistrar {

	@Override
	public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
		// Register resources
		hints.resources().registerPattern("db/*"); // https://github.com/spring-projects/spring-boot/issues/32654
		hints.resources().registerPattern("db/**/*"); // Register all database resources
														// recursively
		hints.resources().registerPattern("messages/*");
		hints.resources().registerPattern("mysql-default-conf");

		// Register Flyway migration scripts
		hints.resources().registerPattern("db/migration/*.sql");

		// Register all entity classes for serialization
		hints.serialization().registerType(BaseEntity.class);
		hints.serialization().registerType(NamedEntity.class);
		hints.serialization().registerType(Person.class);
		hints.serialization().registerType(Owner.class);
		hints.serialization().registerType(Pet.class);
		hints.serialization().registerType(PetType.class);
		hints.serialization().registerType(Visit.class);
		hints.serialization().registerType(Specialty.class);
		hints.serialization().registerType(Vet.class);

		// Register all entity classes for reflection with all member categories
		// This ensures fields, methods, constructors, etc. are accessible
		registerEntityForReflection(hints, BaseEntity.class);
		registerEntityForReflection(hints, NamedEntity.class);
		registerEntityForReflection(hints, Person.class);
		registerEntityForReflection(hints, Owner.class);
		registerEntityForReflection(hints, Pet.class);
		registerEntityForReflection(hints, PetType.class);
		registerEntityForReflection(hints, Visit.class);
		registerEntityForReflection(hints, Specialty.class);
		registerEntityForReflection(hints, Vet.class);

		// Register repository interfaces for proxy generation
		hints.proxies().registerJdkProxy(OwnerRepository.class);
		hints.proxies().registerJdkProxy(PetTypeRepository.class);
		hints.proxies().registerJdkProxy(VetRepository.class);

		// Register repository interfaces for reflection
		registerForReflection(hints, OwnerRepository.class);
		registerForReflection(hints, PetTypeRepository.class);
		registerForReflection(hints, VetRepository.class);

		// Register Flyway classes for reflection
		registerForReflection(hints, Flyway.class);
		registerForReflection(hints, JavaMigration.class);

		// Register Flyway internal classes that are accessed via reflection
		try {
			registerForReflection(hints, Class.forName("org.flywaydb.core.internal.database.h2.H2Database"));
			registerForReflection(hints, Class.forName("org.flywaydb.core.internal.database.h2.H2Schema"));
			registerForReflection(hints, Class.forName("org.flywaydb.core.internal.database.h2.H2Table"));
			registerForReflection(hints, Class.forName("org.flywaydb.core.internal.database.h2.H2Connection"));
		}
		catch (ClassNotFoundException e) {
			// Ignore if classes not found
		}

		// Test-specific reflection configuration has been moved to
		// src/test/resources/reflection-config.json
	}

	/**
	 * Helper method to register an entity class for reflection with all member
	 * categories.
	 */
	private void registerEntityForReflection(RuntimeHints hints, Class<?> entityClass) {
		hints.reflection()
			.registerType(entityClass, MemberCategory.DECLARED_FIELDS, MemberCategory.INVOKE_DECLARED_CONSTRUCTORS,
					MemberCategory.INVOKE_DECLARED_METHODS, MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS,
					MemberCategory.INVOKE_PUBLIC_METHODS, MemberCategory.PUBLIC_FIELDS);
	}

	/**
	 * Helper method to register a class for reflection with basic member categories.
	 */
	private void registerForReflection(RuntimeHints hints, Class<?> clazz) {
		hints.reflection()
			.registerType(clazz, MemberCategory.INVOKE_DECLARED_METHODS, MemberCategory.INVOKE_PUBLIC_METHODS);
	}

}
