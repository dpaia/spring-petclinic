package org.springframework.samples.petclinic.owner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class VisitControllerLoadPetWithVisitTest {

	@Mock
	OwnerRepository owners;

	@InjectMocks
	VisitController controller;

	@Test
	void shouldLoadPetAndCreateVisit() {
		Owner owner = new Owner();
		owner.setId(1);
		Pet pet = new Pet();
		pet.setName("Buddy");
		pet.setBirthDate(LocalDate.of(2020, 1, 1));
		PetType type = new PetType();
		type.setName("dog");
		pet.setType(type);
		owner.addPet(pet);
		pet.setId(7);
		when(owners.findById(1)).thenReturn(Optional.of(owner));

		Map<String, Object> model = new HashMap<>();
		Visit result = controller.loadPetWithVisit(1, 7, model);

		assertThat(result).isNotNull();
		assertThat(model.get("pet")).isEqualTo(pet);
		assertThat(model.get("owner")).isEqualTo(owner);
		assertThat(pet.getVisits()).hasSize(1).contains(result);
	}

	@Test
	void shouldThrowWhenOwnerNotFound() {
		when(owners.findById(999)).thenReturn(Optional.empty());

		Map<String, Object> model = new HashMap<>();

		assertThatThrownBy(() -> controller.loadPetWithVisit(999, 1, model))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining("Owner not found");
	}

}
