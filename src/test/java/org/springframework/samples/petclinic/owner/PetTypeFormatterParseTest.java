package org.springframework.samples.petclinic.owner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import java.text.ParseException;
import java.util.List;
import java.util.Locale;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PetTypeFormatterParseTest {

	@Mock
	PetTypeRepository types;

	@InjectMocks
	PetTypeFormatter formatter;

	@Test
	void shouldParseKnownPetType() throws ParseException {
		PetType cat = new PetType();
		cat.setName("cat");
		PetType dog = new PetType();
		dog.setName("dog");
		when(types.findPetTypes()).thenReturn(List.of(cat, dog));

		PetType result = formatter.parse("dog", Locale.ENGLISH);

		assertThat(result).isSameAs(dog);
		verify(types).findPetTypes();
	}

	@Test
	void shouldThrowParseExceptionForUnknownType() {
		PetType cat = new PetType();
		cat.setName("cat");
		when(types.findPetTypes()).thenReturn(List.of(cat));

		assertThatThrownBy(() -> formatter.parse("lizard", Locale.ENGLISH)).isInstanceOf(ParseException.class)
			.hasMessageContaining("type not found");
		verify(types).findPetTypes();
	}

}
