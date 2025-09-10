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

import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.samples.petclinic.model.BaseEntity;
import org.springframework.samples.petclinic.model.NamedEntity;
import org.springframework.samples.petclinic.model.Person;
import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.samples.petclinic.owner.OwnerRepository;
import org.springframework.samples.petclinic.owner.Pet;
import org.springframework.samples.petclinic.owner.PetType;
import org.springframework.samples.petclinic.owner.PetTypeFormatter;
import org.springframework.samples.petclinic.owner.PetTypeRepository;
import org.springframework.samples.petclinic.owner.PetValidator;
import org.springframework.samples.petclinic.owner.Visit;
import org.springframework.samples.petclinic.system.WebConfiguration;
import org.springframework.samples.petclinic.vet.Specialty;
import org.springframework.samples.petclinic.vet.Vet;
import org.springframework.samples.petclinic.vet.VetRepository;
import org.springframework.samples.petclinic.vet.Vets;

public class PetClinicRuntimeHints implements RuntimeHintsRegistrar {

	@Override
	public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
		// Register resource patterns
		hints.resources().registerPattern("db/*"); // https://github.com/spring-projects/spring-boot/issues/32654
		hints.resources().registerPattern("db/h2/*");
		hints.resources().registerPattern("db/hsqldb/*");
		hints.resources().registerPattern("db/mysql/*");
		hints.resources().registerPattern("db/postgres/*");
		hints.resources().registerPattern("messages/*");
		hints.resources().registerPattern("mysql-default-conf");
		hints.resources().registerPattern("static/**/*");
		hints.resources().registerPattern("templates/**/*");

		// Register model classes for serialization
		registerModelClasses(hints);

		// Register repositories for reflection
		registerRepositories(hints);

		// Register configuration classes for reflection
		registerConfigurations(hints);

		// Register formatters and validators for reflection
		registerFormattersAndValidators(hints);
	}

	private void registerModelClasses(RuntimeHints hints) {
		// Base model classes
		hints.reflection()
			.registerType(BaseEntity.class, MemberCategory.DECLARED_FIELDS, MemberCategory.INVOKE_PUBLIC_METHODS);
		hints.reflection()
			.registerType(NamedEntity.class, MemberCategory.DECLARED_FIELDS, MemberCategory.INVOKE_PUBLIC_METHODS);
		hints.reflection()
			.registerType(Person.class, MemberCategory.DECLARED_FIELDS, MemberCategory.INVOKE_PUBLIC_METHODS);

		// Owner model classes
		hints.reflection()
			.registerType(Owner.class, MemberCategory.DECLARED_FIELDS, MemberCategory.INVOKE_PUBLIC_METHODS);
		hints.reflection()
			.registerType(Pet.class, MemberCategory.DECLARED_FIELDS, MemberCategory.INVOKE_PUBLIC_METHODS);
		hints.reflection()
			.registerType(PetType.class, MemberCategory.DECLARED_FIELDS, MemberCategory.INVOKE_PUBLIC_METHODS);
		hints.reflection()
			.registerType(Visit.class, MemberCategory.DECLARED_FIELDS, MemberCategory.INVOKE_PUBLIC_METHODS);

		// Vet model classes
		hints.reflection()
			.registerType(Specialty.class, MemberCategory.DECLARED_FIELDS, MemberCategory.INVOKE_PUBLIC_METHODS);
		hints.reflection()
			.registerType(Vet.class, MemberCategory.DECLARED_FIELDS, MemberCategory.INVOKE_PUBLIC_METHODS);
		hints.reflection()
			.registerType(Vets.class, MemberCategory.DECLARED_FIELDS, MemberCategory.INVOKE_PUBLIC_METHODS);

		// Register for serialization
		hints.serialization().registerType(BaseEntity.class);
		hints.serialization().registerType(NamedEntity.class);
		hints.serialization().registerType(Person.class);
		hints.serialization().registerType(Owner.class);
		hints.serialization().registerType(Pet.class);
		hints.serialization().registerType(PetType.class);
		hints.serialization().registerType(Visit.class);
		hints.serialization().registerType(Specialty.class);
		hints.serialization().registerType(Vet.class);
	}

	private void registerRepositories(RuntimeHints hints) {
		// Owner repositories
		hints.reflection().registerType(OwnerRepository.class, MemberCategory.INVOKE_PUBLIC_METHODS);
		hints.reflection().registerType(PetTypeRepository.class, MemberCategory.INVOKE_PUBLIC_METHODS);

		// Vet repositories
		hints.reflection().registerType(VetRepository.class, MemberCategory.INVOKE_PUBLIC_METHODS);
	}

	private void registerConfigurations(RuntimeHints hints) {
		// System configurations
		hints.reflection()
			.registerType(WebConfiguration.class, MemberCategory.DECLARED_FIELDS,
					MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS);
	}

	private void registerFormattersAndValidators(RuntimeHints hints) {
		// Formatters
		hints.reflection()
			.registerType(PetTypeFormatter.class, MemberCategory.DECLARED_FIELDS, MemberCategory.INVOKE_PUBLIC_METHODS);

		// Validators
		hints.reflection()
			.registerType(PetValidator.class, MemberCategory.DECLARED_FIELDS, MemberCategory.INVOKE_PUBLIC_METHODS);
	}

}
