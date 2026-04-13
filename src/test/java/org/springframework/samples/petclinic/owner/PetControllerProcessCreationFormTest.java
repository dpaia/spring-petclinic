package org.springframework.samples.petclinic.owner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ExtendWith(MockitoExtension.class)
class PetControllerProcessCreationFormTest {

	@Mock
	OwnerRepository owners;

	@Mock
	PetTypeRepository types;

	@InjectMocks
	PetController controller;

	@Mock
	BindingResult bindingResult;

	@Mock
	RedirectAttributes redirectAttributes;

	@Test
	void shouldAddPetAndRedirect() {
		Owner owner = new Owner();
		owner.setId(1);
		Pet pet = new Pet();
		pet.setName("Buddy");
		pet.setBirthDate(LocalDate.of(2020, 1, 1));
		PetType type = new PetType();
		type.setName("dog");
		pet.setType(type);
		when(bindingResult.hasErrors()).thenReturn(false);

		String view = controller.processCreationForm(owner, pet, bindingResult, redirectAttributes);

		assertThat(view).isEqualTo("redirect:/owners/{ownerId}");
		assertThat(owner.getPets()).contains(pet);
		verify(owners).save(owner);
		verify(redirectAttributes).addFlashAttribute("message", "New Pet has been Added");
	}

	@Test
	void shouldReturnFormViewWhenErrors() {
		Owner owner = new Owner();
		Pet pet = new Pet();
		when(bindingResult.hasErrors()).thenReturn(true);

		String view = controller.processCreationForm(owner, pet, bindingResult, redirectAttributes);

		assertThat(view).isEqualTo("pets/createOrUpdatePetForm");
		verify(owners, never()).save(any());
	}

	@Test
	void shouldRejectDuplicatePetName() {
		Owner owner = new Owner();
		Pet existingPet = new Pet();
		existingPet.setName("Buddy");
		PetType type = new PetType();
		type.setName("dog");
		existingPet.setType(type);
		existingPet.setBirthDate(LocalDate.of(2019, 1, 1));
		owner.addPet(existingPet);
		existingPet.setId(7);

		Pet newPet = new Pet();
		newPet.setName("Buddy");
		newPet.setBirthDate(LocalDate.of(2020, 1, 1));
		newPet.setType(type);
		when(bindingResult.hasErrors()).thenReturn(true);

		String view = controller.processCreationForm(owner, newPet, bindingResult, redirectAttributes);

		assertThat(view).isEqualTo("pets/createOrUpdatePetForm");
		verify(bindingResult).rejectValue("name", "duplicate", "already exists");
		verify(owners, never()).save(any());
	}

	@Test
	void shouldRejectFutureBirthDate() {
		Owner owner = new Owner();
		owner.setId(1);
		Pet pet = new Pet();
		pet.setName("Buddy");
		pet.setBirthDate(LocalDate.now().plusDays(1));
		PetType type = new PetType();
		type.setName("dog");
		pet.setType(type);
		when(bindingResult.hasErrors()).thenReturn(true);

		String view = controller.processCreationForm(owner, pet, bindingResult, redirectAttributes);

		assertThat(view).isEqualTo("pets/createOrUpdatePetForm");
		verify(bindingResult).rejectValue("birthDate", "typeMismatch.birthDate");
		verify(owners, never()).save(any());
	}

}
