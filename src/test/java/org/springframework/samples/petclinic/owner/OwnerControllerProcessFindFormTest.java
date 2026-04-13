package org.springframework.samples.petclinic.owner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

@ExtendWith(MockitoExtension.class)
class OwnerControllerProcessFindFormTest {

	@Mock
	OwnerRepository owners;

	@InjectMocks
	OwnerController controller;

	@Mock
	BindingResult bindingResult;

	@Test
	void shouldReturnFindOwnersViewWhenNoOwnersFound() {
		Owner owner = new Owner();
		owner.setLastName("Unknown");
		when(owners.findByLastNameStartingWith(eq("Unknown"), any(Pageable.class))).thenReturn(Page.empty());

		Model model = new ExtendedModelMap();
		String view = controller.processFindForm(1, owner, bindingResult, model);

		assertThat(view).isEqualTo("owners/findOwners");
		verify(bindingResult).rejectValue("lastName", "notFound", "not found");
		verify(owners).findByLastNameStartingWith(eq("Unknown"), any(Pageable.class));
	}

	@Test
	void shouldRedirectToOwnerWhenSingleResult() {
		Owner owner = new Owner();
		owner.setLastName("Smith");
		Owner found = new Owner();
		found.setId(42);
		found.setLastName("Smith");
		when(owners.findByLastNameStartingWith(eq("Smith"), any(Pageable.class)))
			.thenReturn(new PageImpl<>(List.of(found)));

		Model model = new ExtendedModelMap();
		String view = controller.processFindForm(1, owner, bindingResult, model);

		assertThat(view).isEqualTo("redirect:/owners/42");
		verify(owners).findByLastNameStartingWith(eq("Smith"), any(Pageable.class));
	}

	@Test
	void shouldReturnOwnersListWhenMultipleResults() {
		Owner owner = new Owner();
		owner.setLastName("Sm");
		Owner o1 = new Owner();
		o1.setId(1);
		Owner o2 = new Owner();
		o2.setId(2);
		Page<Owner> page = new PageImpl<>(List.of(o1, o2), PageRequest.of(1, 5), 7);
		when(owners.findByLastNameStartingWith(eq("Sm"), any(Pageable.class))).thenReturn(page);

		Model model = new ExtendedModelMap();
		String view = controller.processFindForm(2, owner, bindingResult, model);

		assertThat(view).isEqualTo("owners/ownersList");
		assertThat(model.getAttribute("currentPage")).isEqualTo(2);
		assertThat(model.getAttribute("totalPages")).isEqualTo(2);
		assertThat(model.getAttribute("totalItems")).isEqualTo(7L);
		assertThat(model.getAttribute("listOwners")).isEqualTo(List.of(o1, o2));
		var pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
		verify(owners).findByLastNameStartingWith(eq("Sm"), pageableCaptor.capture());
		assertThat(pageableCaptor.getValue().getPageNumber()).isEqualTo(1);
		assertThat(pageableCaptor.getValue().getPageSize()).isEqualTo(5);
	}

	@Test
	void shouldSetEmptyLastNameWhenNull() {
		Owner owner = new Owner();
		// lastName is null by default
		Owner found = new Owner();
		found.setId(1);
		when(owners.findByLastNameStartingWith(eq(""), any(Pageable.class))).thenReturn(new PageImpl<>(List.of(found)));

		Model model = new ExtendedModelMap();
		controller.processFindForm(1, owner, bindingResult, model);

		assertThat(owner.getLastName()).isEqualTo("");
		verify(owners).findByLastNameStartingWith(eq(""), any(Pageable.class));
	}

}
