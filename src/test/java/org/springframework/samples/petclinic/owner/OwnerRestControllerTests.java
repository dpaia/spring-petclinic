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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for {@link OwnerRestController}
 */
@WebMvcTest(OwnerRestController.class)
class OwnerRestControllerTests {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private OwnerRepository ownerRepository;

	@BeforeEach
	void setup() {
		JacksonTester.initFields(this, new ObjectMapper());
	}

	@Test
	void testGetAllOwners() throws Exception {
		// given
		Owner owner1 = new Owner();
		owner1.setId(1);
		owner1.setFirstName("George");
		owner1.setLastName("Franklin");
		owner1.setAddress("110 W. Liberty St.");
		owner1.setCity("Madison");
		owner1.setTelephone("6085551023");
		owner1.setEmail("george.franklin@example.com");

		Owner owner2 = new Owner();
		owner2.setId(2);
		owner2.setFirstName("Betty");
		owner2.setLastName("Davis");
		owner2.setAddress("638 Cardinal Ave.");
		owner2.setCity("Sun Prairie");
		owner2.setTelephone("6085551749");
		owner2.setEmail("betty.davis@example.com");

		List<Owner> owners = Arrays.asList(owner1, owner2);
		Page<Owner> ownersPage = new PageImpl<>(owners, PageRequest.of(0, 10), owners.size());

		given(this.ownerRepository.findAll(any(Pageable.class))).willReturn(ownersPage);

		// when
		mockMvc.perform(get("/api/v1/owners"))
			// then
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.content", hasSize(2)))
			.andExpect(jsonPath("$.content[0].id", is(1)))
			.andExpect(jsonPath("$.content[0].firstName", is("George")))
			.andExpect(jsonPath("$.content[0].lastName", is("Franklin")))
			.andExpect(jsonPath("$.content[0].email", is("george.franklin@example.com")))
			.andExpect(jsonPath("$.content[1].id", is(2)))
			.andExpect(jsonPath("$.content[1].firstName", is("Betty")))
			.andExpect(jsonPath("$.content[1].lastName", is("Davis")))
			.andExpect(jsonPath("$.content[1].email", is("betty.davis@example.com")));
	}

	@Test
	void testGetOwnersByLastName() throws Exception {
		// given
		Owner owner = new Owner();
		owner.setId(2);
		owner.setFirstName("Betty");
		owner.setLastName("Davis");
		owner.setAddress("638 Cardinal Ave.");
		owner.setCity("Sun Prairie");
		owner.setTelephone("6085551749");
		owner.setEmail("betty.davis@example.com");

		List<Owner> owners = List.of(owner);
		Page<Owner> ownersPage = new PageImpl<>(owners, PageRequest.of(0, 10), owners.size());

		given(this.ownerRepository.findByLastNameStartingWith(eq("Davis"), any(Pageable.class))).willReturn(ownersPage);

		// when
		mockMvc.perform(get("/api/v1/owners?lastName=Davis"))
			// then
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.content", hasSize(1)))
			.andExpect(jsonPath("$.content[0].id", is(2)))
			.andExpect(jsonPath("$.content[0].firstName", is("Betty")))
			.andExpect(jsonPath("$.content[0].lastName", is("Davis")))
			.andExpect(jsonPath("$.content[0].email", is("betty.davis@example.com")));
	}

	@Test
	void testGetOwner() throws Exception {
		// given
		Owner owner = new Owner();
		owner.setId(1);
		owner.setFirstName("George");
		owner.setLastName("Franklin");
		owner.setAddress("110 W. Liberty St.");
		owner.setCity("Madison");
		owner.setTelephone("6085551023");

		given(this.ownerRepository.findById(1)).willReturn(Optional.of(owner));

		// when
		mockMvc.perform(get("/api/v1/owners/1"))
			// then
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.id", is(1)))
			.andExpect(jsonPath("$.firstName", is("George")))
			.andExpect(jsonPath("$.lastName", is("Franklin")))
			.andExpect(jsonPath("$.address", is("110 W. Liberty St.")))
			.andExpect(jsonPath("$.city", is("Madison")))
			.andExpect(jsonPath("$.telephone", is("6085551023")));
	}

	@Test
	void testGetOwnerNotFound() throws Exception {
		// given
		given(this.ownerRepository.findById(999)).willReturn(Optional.empty());

		// when
		mockMvc.perform(get("/api/v1/owners/999"))
			// then
			.andExpect(status().isNotFound());
	}

	@Test
	void testCreateOwner() throws Exception {
		// given
		Owner newOwner = new Owner();
		newOwner.setFirstName("John");
		newOwner.setLastName("Doe");
		newOwner.setAddress("123 Main St");
		newOwner.setCity("Anytown");
		newOwner.setTelephone("1234567890");
		newOwner.setEmail("john.doe@example.com");

		given(this.ownerRepository.save(any(Owner.class))).willAnswer(invocation -> {
			Owner savedOwner = invocation.getArgument(0);
			savedOwner.setId(10);
			return savedOwner;
		});

		// when
		mockMvc.perform(post("/api/v1/owners").contentType(MediaType.APPLICATION_JSON)
			.content(
					"{\"firstName\":\"John\",\"lastName\":\"Doe\",\"address\":\"123 Main St\",\"city\":\"Anytown\",\"telephone\":\"1234567890\",\"email\":\"john.doe@example.com\"}"))
			// then
			.andExpect(status().isCreated())
			.andExpect(header().string("Location", containsString("/api/v1/owners/10")))
			.andExpect(jsonPath("$.id", is(10)))
			.andExpect(jsonPath("$.firstName", is("John")))
			.andExpect(jsonPath("$.lastName", is("Doe")))
			.andExpect(jsonPath("$.address", is("123 Main St")))
			.andExpect(jsonPath("$.city", is("Anytown")))
			.andExpect(jsonPath("$.telephone", is("1234567890")))
			.andExpect(jsonPath("$.email", is("john.doe@example.com")));

		verify(this.ownerRepository).save(any(Owner.class));
	}

	@Test
	void testCreateOwnerWithId() throws Exception {
		// when
		mockMvc.perform(post("/api/v1/owners").contentType(MediaType.APPLICATION_JSON)
			.content(
					"{\"id\":1,\"firstName\":\"John\",\"lastName\":\"Doe\",\"address\":\"123 Main St\",\"city\":\"Anytown\",\"telephone\":\"1234567890\"}"))
			// then
			.andExpect(status().isBadRequest());

		verify(this.ownerRepository, never()).save(any(Owner.class));
	}

	@Test
	void testCreateOwnerInvalidData() throws Exception {
		// when - missing required fields
		mockMvc
			.perform(post("/api/v1/owners").contentType(MediaType.APPLICATION_JSON).content("{\"firstName\":\"John\"}"))
			// then
			.andExpect(status().isBadRequest());

		verify(this.ownerRepository, never()).save(any(Owner.class));
	}

	@Test
	void testUpdateOwner() throws Exception {
		// given
		Owner existingOwner = new Owner();
		existingOwner.setId(1);
		existingOwner.setFirstName("George");
		existingOwner.setLastName("Franklin");
		existingOwner.setAddress("110 W. Liberty St.");
		existingOwner.setCity("Madison");
		existingOwner.setTelephone("6085551023");
		existingOwner.setEmail("george.franklin@example.com");

		given(this.ownerRepository.findById(1)).willReturn(Optional.of(existingOwner));
		given(this.ownerRepository.save(any(Owner.class))).willAnswer(invocation -> invocation.getArgument(0));

		// when
		mockMvc.perform(put("/api/v1/owners/1").contentType(MediaType.APPLICATION_JSON)
			.content(
					"{\"firstName\":\"George Updated\",\"lastName\":\"Franklin\",\"address\":\"110 W. Liberty St.\",\"city\":\"Madison\",\"telephone\":\"6085551023\",\"email\":\"george.franklin@example.com\"}"))
			// then
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id", is(1)))
			.andExpect(jsonPath("$.firstName", is("George Updated")))
			.andExpect(jsonPath("$.lastName", is("Franklin")))
			.andExpect(jsonPath("$.address", is("110 W. Liberty St.")))
			.andExpect(jsonPath("$.city", is("Madison")))
			.andExpect(jsonPath("$.telephone", is("6085551023")))
			.andExpect(jsonPath("$.email", is("george.franklin@example.com")));

		verify(this.ownerRepository).save(any(Owner.class));
	}

	@Test
	void testUpdateOwnerNotFound() throws Exception {
		// given
		given(this.ownerRepository.findById(999)).willReturn(Optional.empty());

		// when
		mockMvc.perform(put("/api/v1/owners/999").contentType(MediaType.APPLICATION_JSON)
			.content(
					"{\"firstName\":\"George Updated\",\"lastName\":\"Franklin\",\"address\":\"110 W. Liberty St.\",\"city\":\"Madison\",\"telephone\":\"6085551023\",\"email\":\"george.franklin@example.com\"}"))
			// then
			.andExpect(status().isNotFound());

		verify(this.ownerRepository, never()).save(any(Owner.class));
	}

	@Test
	void testUpdateOwnerIdMismatch() throws Exception {
		// given
		Owner existingOwner = new Owner();
		existingOwner.setId(1);
		existingOwner.setFirstName("George");
		existingOwner.setLastName("Franklin");
		existingOwner.setAddress("110 W. Liberty St.");
		existingOwner.setCity("Madison");
		existingOwner.setTelephone("6085551023");

		given(this.ownerRepository.findById(1)).willReturn(Optional.of(existingOwner));

		// when
		mockMvc.perform(put("/api/v1/owners/1").contentType(MediaType.APPLICATION_JSON)
			.content(
					"{\"id\":2,\"firstName\":\"George Updated\",\"lastName\":\"Franklin\",\"address\":\"110 W. Liberty St.\",\"city\":\"Madison\",\"telephone\":\"6085551023\"}"))
			// then
			.andExpect(status().isBadRequest());

		verify(this.ownerRepository, never()).save(any(Owner.class));
	}

	@Test
	void testDeleteOwner() throws Exception {
		// given
		given(this.ownerRepository.existsById(1)).willReturn(true);

		// when
		mockMvc.perform(delete("/api/v1/owners/1"))
			// then
			.andExpect(status().isNoContent());

		verify(this.ownerRepository).deleteById(1);
	}

	@Test
	void testDeleteOwnerNotFound() throws Exception {
		// given
		given(this.ownerRepository.existsById(999)).willReturn(false);

		// when
		mockMvc.perform(delete("/api/v1/owners/999"))
			// then
			.andExpect(status().isNotFound());

		verify(this.ownerRepository, never()).deleteById(anyInt());
	}

}
