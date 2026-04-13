package org.springframework.samples.petclinic.owner;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.HashMap;
import org.junit.jupiter.api.Test;
import org.springframework.validation.MapBindingResult;

class PetValidatorValidateTest {

	private final PetValidator validator = new PetValidator();

	@Test
	void shouldPassForValidPet() {
		Pet pet = new Pet();
		pet.setName("Buddy");
		pet.setBirthDate(LocalDate.of(2020, 1, 1));
		PetType type = new PetType();
		type.setName("dog");
		pet.setType(type);
		MapBindingResult errors = new MapBindingResult(new HashMap<>(), "pet");

		validator.validate(pet, errors);

		assertThat(errors.hasErrors()).isFalse();
	}

	@Test
	void shouldRejectBlankName() {
		Pet pet = new Pet();
		pet.setName("");
		pet.setBirthDate(LocalDate.of(2020, 1, 1));
		PetType type = new PetType();
		type.setName("dog");
		pet.setType(type);
		MapBindingResult errors = new MapBindingResult(new HashMap<>(), "pet");

		validator.validate(pet, errors);

		assertThat(errors.getFieldError("name")).isNotNull();
	}

	@Test
	void shouldRejectNullTypeForNewPet() {
		Pet pet = new Pet();
		pet.setName("Buddy");
		pet.setBirthDate(LocalDate.of(2020, 1, 1));
		// type is null, pet is new (no ID)
		MapBindingResult errors = new MapBindingResult(new HashMap<>(), "pet");

		validator.validate(pet, errors);

		assertThat(errors.getFieldError("type")).isNotNull();
	}

	@Test
	void shouldAllowNullTypeForExistingPet() {
		Pet pet = new Pet();
		pet.setId(1);
		pet.setName("Buddy");
		pet.setBirthDate(LocalDate.of(2020, 1, 1));
		MapBindingResult errors = new MapBindingResult(new HashMap<>(), "pet");

		validator.validate(pet, errors);

		assertThat(errors.getFieldError("type")).isNull();
		assertThat(errors.hasErrors()).isFalse();
	}

	@Test
	void shouldRejectNullBirthDate() {
		Pet pet = new Pet();
		pet.setName("Buddy");
		PetType type = new PetType();
		type.setName("dog");
		pet.setType(type);
		// birthDate is null
		MapBindingResult errors = new MapBindingResult(new HashMap<>(), "pet");

		validator.validate(pet, errors);

		assertThat(errors.getFieldError("birthDate")).isNotNull();
	}

}
