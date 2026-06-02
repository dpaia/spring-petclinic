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
package org.springframework.samples.petclinic.owner;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for {@link VisitRestController}
 */
@WebMvcTest(VisitRestController.class)
class VisitRestControllerTests {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private VisitRepository visitRepository;

	@MockitoBean
	private PetRepository petRepository;

	@BeforeEach
	void setup() {
		JacksonTester.initFields(this, new ObjectMapper());
	}

	@Test
	void testGetAllVisits() throws Exception {
		// given
		Visit visit1 = new Visit();
		visit1.setId(1);
		visit1.setDate(LocalDate.of(2023, 1, 1));
		visit1.setDescription("Annual checkup");

		Visit visit2 = new Visit();
		visit2.setId(2);
		visit2.setDate(LocalDate.of(2023, 2, 15));
		visit2.setDescription("Rabies vaccination");

		List<Visit> visits = Arrays.asList(visit1, visit2);
		Page<Visit> visitsPage = new PageImpl<>(visits, PageRequest.of(0, 10), visits.size());
		given(this.visitRepository.findAll(any(Pageable.class))).willReturn(visitsPage);

		// when
		mockMvc.perform(get("/api/v1/visits"))
			// then
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.content", hasSize(2)))
			.andExpect(jsonPath("$.content[0].id", is(1)))
			.andExpect(jsonPath("$.content[0].date", is("2023-01-01")))
			.andExpect(jsonPath("$.content[0].description", is("Annual checkup")))
			.andExpect(jsonPath("$.content[1].id", is(2)))
			.andExpect(jsonPath("$.content[1].date", is("2023-02-15")))
			.andExpect(jsonPath("$.content[1].description", is("Rabies vaccination")))
			.andExpect(jsonPath("$.pageable.pageNumber", is(0)))
			.andExpect(jsonPath("$.pageable.pageSize", is(10)))
			.andExpect(jsonPath("$.totalElements", is(2)));

		// Test with custom page and size parameters
		mockMvc.perform(get("/api/v1/visits?page=2&size=5"))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.content", hasSize(2)));

		verify(this.visitRepository, times(2)).findAll(any(Pageable.class));
	}

	@Test
	void testGetVisit() throws Exception {
		// given
		Visit visit = new Visit();
		visit.setId(1);
		visit.setDate(LocalDate.of(2023, 1, 1));
		visit.setDescription("Annual checkup");

		given(this.visitRepository.findById(1)).willReturn(Optional.of(visit));

		// when
		mockMvc.perform(get("/api/v1/visits/1"))
			// then
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.id", is(1)))
			.andExpect(jsonPath("$.date", is("2023-01-01")))
			.andExpect(jsonPath("$.description", is("Annual checkup")));
	}

	@Test
	void testGetVisitNotFound() throws Exception {
		// given
		given(this.visitRepository.findById(999)).willReturn(Optional.empty());

		// when
		mockMvc.perform(get("/api/v1/visits/999"))
			// then
			.andExpect(status().isNotFound());
	}

	@Test
	void testGetVisitsByPet() throws Exception {
		// given
		Visit visit1 = new Visit();
		visit1.setId(1);
		visit1.setDate(LocalDate.of(2023, 1, 1));
		visit1.setDescription("Annual checkup");

		Visit visit2 = new Visit();
		visit2.setId(2);
		visit2.setDate(LocalDate.of(2023, 2, 15));
		visit2.setDescription("Rabies vaccination");

		List<Visit> visits = Arrays.asList(visit1, visit2);
		Page<Visit> visitsPage = new PageImpl<>(visits, PageRequest.of(0, 10), visits.size());
		given(this.petRepository.existsById(1)).willReturn(true);
		given(this.visitRepository.findByPetId(eq(1), any(Pageable.class))).willReturn(visitsPage);

		// when
		mockMvc.perform(get("/api/v1/pets/1/visits"))
			// then
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.content", hasSize(2)))
			.andExpect(jsonPath("$.content[0].id", is(1)))
			.andExpect(jsonPath("$.content[0].date", is("2023-01-01")))
			.andExpect(jsonPath("$.content[0].description", is("Annual checkup")))
			.andExpect(jsonPath("$.content[1].id", is(2)))
			.andExpect(jsonPath("$.content[1].date", is("2023-02-15")))
			.andExpect(jsonPath("$.content[1].description", is("Rabies vaccination")))
			.andExpect(jsonPath("$.pageable.pageNumber", is(0)))
			.andExpect(jsonPath("$.pageable.pageSize", is(10)))
			.andExpect(jsonPath("$.totalElements", is(2)));

		// Test with custom page and size parameters
		mockMvc.perform(get("/api/v1/pets/1/visits?page=2&size=5"))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.content", hasSize(2)));

		verify(this.visitRepository, times(2)).findByPetId(eq(1), any(Pageable.class));
	}

	@Test
	void testGetVisitsByPetNotFound() throws Exception {
		// given
		given(this.petRepository.existsById(999)).willReturn(false);

		// when
		mockMvc.perform(get("/api/v1/pets/999/visits"))
			// then
			.andExpect(status().isNotFound());
	}

	@Test
	void testAddVisit() throws Exception {
		// given
		Pet pet = new Pet();
		pet.setId(1);
		pet.setName("Leo");

		Visit newVisit = new Visit();
		newVisit.setDate(LocalDate.of(2023, 3, 10));
		newVisit.setDescription("Dental cleaning");

		given(this.petRepository.findById(1)).willReturn(Optional.of(pet));
		given(this.petRepository.save(any(Pet.class))).willAnswer(invocation -> {
			Pet savedPet = invocation.getArgument(0);
			Visit savedVisit = savedPet.getVisits().iterator().next();
			savedVisit.setId(5);
			return savedPet;
		});

		// when
		mockMvc
			.perform(post("/api/v1/pets/1/visits").contentType(MediaType.APPLICATION_JSON)
				.content("{\"date\":\"2023-03-10\",\"description\":\"Dental cleaning\"}"))
			// then
			.andExpect(status().isCreated())
			.andExpect(header().string("Location", containsString("/api/v1/pets/1/visits/5")))
			.andExpect(jsonPath("$.date", is("2023-03-10")))
			.andExpect(jsonPath("$.description", is("Dental cleaning")));

		verify(this.petRepository).save(any(Pet.class));
	}

	@Test
	void testAddVisitPetNotFound() throws Exception {
		// given
		given(this.petRepository.findById(999)).willReturn(Optional.empty());

		// when
		mockMvc
			.perform(post("/api/v1/pets/999/visits").contentType(MediaType.APPLICATION_JSON)
				.content("{\"date\":\"2023-03-10\",\"description\":\"Dental cleaning\"}"))
			// then
			.andExpect(status().isNotFound());

		verify(this.petRepository, never()).save(any(Pet.class));
	}

	@Test
	void testAddVisitInvalidDescription() throws Exception {
		// given
		Pet pet = new Pet();
		pet.setId(1);
		pet.setName("Leo");

		given(this.petRepository.findById(1)).willReturn(Optional.of(pet));

		// when
		mockMvc
			.perform(post("/api/v1/pets/1/visits").contentType(MediaType.APPLICATION_JSON)
				.content("{\"date\":\"2023-03-10\",\"description\":\"\"}"))
			// then
			.andExpect(status().isBadRequest());

		verify(this.petRepository, never()).save(any(Pet.class));
	}

	@Test
	void testUpdateVisit() throws Exception {
		// given
		Visit visit = new Visit();
		visit.setId(1);
		visit.setDate(LocalDate.of(2023, 1, 1));
		visit.setDescription("Annual checkup");

		given(this.visitRepository.findById(1)).willReturn(Optional.of(visit));
		given(this.visitRepository.save(any(Visit.class))).willAnswer(invocation -> invocation.getArgument(0));

		// when
		mockMvc
			.perform(put("/api/v1/visits/1").contentType(MediaType.APPLICATION_JSON)
				.content("{\"date\":\"2023-01-02\",\"description\":\"Annual checkup updated\"}"))
			// then
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id", is(1)))
			.andExpect(jsonPath("$.date", is("2023-01-02")))
			.andExpect(jsonPath("$.description", is("Annual checkup updated")));

		verify(this.visitRepository).save(any(Visit.class));
	}

	@Test
	void testUpdateVisitNotFound() throws Exception {
		// given
		given(this.visitRepository.findById(999)).willReturn(Optional.empty());

		// when
		mockMvc
			.perform(put("/api/v1/visits/999").contentType(MediaType.APPLICATION_JSON)
				.content("{\"date\":\"2023-01-02\",\"description\":\"Annual checkup updated\"}"))
			// then
			.andExpect(status().isNotFound());

		verify(this.visitRepository, never()).save(any(Visit.class));
	}

	@Test
	void testDeleteVisit() throws Exception {
		// given
		given(this.visitRepository.existsById(1)).willReturn(true);

		// when
		mockMvc.perform(delete("/api/v1/visits/1"))
			// then
			.andExpect(status().isNoContent());

		verify(this.visitRepository).deleteById(1);
	}

	@Test
	void testDeleteVisitNotFound() throws Exception {
		// given
		given(this.visitRepository.existsById(999)).willReturn(false);

		// when
		mockMvc.perform(delete("/api/v1/visits/999"))
			// then
			.andExpect(status().isNotFound());

		verify(this.visitRepository, never()).deleteById(anyInt());
	}

}
