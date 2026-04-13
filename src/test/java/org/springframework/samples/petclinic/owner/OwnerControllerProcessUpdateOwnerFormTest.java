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
class OwnerControllerProcessUpdateOwnerFormTest {

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

		String view = controller.processUpdateOwnerForm(owner, bindingResult, 1, redirectAttributes);

		assertThat(view).isEqualTo("owners/createOrUpdateOwnerForm");
		verify(redirectAttributes).addFlashAttribute("error", "There was an error in updating the owner.");
		verify(owners, never()).save(any());
	}

	@Test
	void shouldUpdateOwnerAndRedirect() {
		when(bindingResult.hasErrors()).thenReturn(false);
		Owner owner = spy(new Owner());
		owner.setId(42);
		clearInvocations(owner);

		String view = controller.processUpdateOwnerForm(owner, bindingResult, 42, redirectAttributes);

		verify(owner).setId(42);
		verify(owners).save(owner);
		assertThat(view).isEqualTo("redirect:/owners/{ownerId}");
		verify(redirectAttributes).addFlashAttribute("message", "Owner Values Updated");
	}

	@Test
	void shouldRejectIdMismatch() {
		when(bindingResult.hasErrors()).thenReturn(false);
		Owner owner = new Owner();
		owner.setId(99);

		String view = controller.processUpdateOwnerForm(owner, bindingResult, 42, redirectAttributes);

		assertThat(view).isEqualTo("redirect:/owners/{ownerId}/edit");
		verify(bindingResult).rejectValue("id", "mismatch", "The owner ID in the form does not match the URL.");
		verify(redirectAttributes).addFlashAttribute("error", "Owner ID mismatch. Please try again.");
		verify(owners, never()).save(any());
	}

}
