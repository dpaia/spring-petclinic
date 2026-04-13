package org.springframework.samples.petclinic.owner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ExtendWith(MockitoExtension.class)
class OwnerControllerProcessCreationFormTest {

	@Mock
	OwnerRepository owners;

	@InjectMocks
	OwnerController controller;

	@Mock
	BindingResult bindingResult;

	@Mock
	RedirectAttributes redirectAttributes;

	@Test
	void shouldReturnFormViewWhenBindingErrors() {
		when(bindingResult.hasErrors()).thenReturn(true);
		Owner owner = new Owner();

		String view = controller.processCreationForm(owner, bindingResult, redirectAttributes);

		assertThat(view).isEqualTo("owners/createOrUpdateOwnerForm");
		verify(redirectAttributes).addFlashAttribute("error", "There was an error in creating the owner.");
		verify(owners, never()).save(any());
	}

	@Test
	void shouldSaveOwnerAndRedirect() {
		when(bindingResult.hasErrors()).thenReturn(false);
		Owner owner = new Owner();
		owner.setId(42);

		String view = controller.processCreationForm(owner, bindingResult, redirectAttributes);

		verify(owners).save(owner);
		assertThat(view).isEqualTo("redirect:/owners/42");
		verify(redirectAttributes).addFlashAttribute("message", "New Owner Created");
	}

}
