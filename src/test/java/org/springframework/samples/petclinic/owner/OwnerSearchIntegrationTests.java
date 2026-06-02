package org.springframework.samples.petclinic.owner;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.aot.DisabledInAotMode;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc
@DisabledInAotMode
class OwnerSearchIntegrationTests {

	@Autowired
	MockMvc mockMvc;

	@MockitoSpyBean
	OwnerRepository ownerRepository;

	@Test
	void whenSingleResult_redirects_withoutFetchingFullOwners() throws Exception {
		mockMvc.perform(get("/owners").param("page", "1").param("lastName", "Franklin"))
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:/owners/1"));

		verify(ownerRepository, times(1)).findIdByLastNameStartingWith(eq("Franklin"), any(Pageable.class));
		verify(ownerRepository, never()).findByLastNameStartingWith(anyString(), any(Pageable.class));
	}

	@Test
	void whenNoResults_showsError_withoutFetchingFullOwners() throws Exception {
		mockMvc.perform(get("/owners").param("page", "1").param("lastName", "Unknown Surname"))
			.andExpect(status().isOk())
			.andExpect(view().name("owners/findOwners"));

		verify(ownerRepository, times(1)).findIdByLastNameStartingWith(eq("Unknown Surname"), any(Pageable.class));
		verify(ownerRepository, never()).findByLastNameStartingWith(anyString(), any(Pageable.class));
	}

	@Test
	void whenMultipleResults_listsOwners_andFetchesFullOwners() throws Exception {
		mockMvc.perform(get("/owners").param("page", "1").param("lastName", "Davis"))
			.andExpect(status().isOk())
			.andExpect(view().name("owners/ownersList"));

		verify(ownerRepository, times(1)).findIdByLastNameStartingWith(eq("Davis"), any(Pageable.class));
		verify(ownerRepository, times(1)).findByLastNameStartingWith(eq("Davis"), any(Pageable.class));
	}
}
