package org.springframework.samples.petclinic.owner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OwnerControllerFindOwnerTest {

	@Mock
	OwnerRepository owners;

	@InjectMocks
	OwnerController controller;

	@Test
	void shouldReturnNewOwnerWhenIdNull() {
		Owner result = controller.findOwner(null);

		assertThat(result).isNotNull();
		assertThat(result.getId()).isNull();
		verifyNoInteractions(owners);
	}

	@Test
	void shouldReturnOwnerWhenFound() {
		Owner owner = new Owner();
		owner.setId(42);
		owner.setLastName("Smith");
		when(owners.findById(42)).thenReturn(Optional.of(owner));

		Owner result = controller.findOwner(42);

		assertThat(result.getId()).isEqualTo(42);
		assertThat(result.getLastName()).isEqualTo("Smith");
		verify(owners).findById(42);
	}

	@Test
	void shouldThrowWhenOwnerNotFound() {
		when(owners.findById(999)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> controller.findOwner(999)).isInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining("Owner not found");
		verify(owners).findById(999);
	}

}
