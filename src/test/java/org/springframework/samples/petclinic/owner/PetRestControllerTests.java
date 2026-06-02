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
import java.util.ArrayList;
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
 * Test class for {@link PetRestController}
 */
@WebMvcTest(PetRestController.class)
class PetRestControllerTests {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private PetRepository petRepository;

	@MockitoBean
	private OwnerRepository ownerRepository;

	@MockitoBean
	private PetTypeRepository petTypeRepository;

	@BeforeEach
	void setup() {
		JacksonTester.initFields(this, new ObjectMapper());
	}

	@Test
	void testGetAllPets() throws Exception {
		// given
		PetType dog = new PetType();
		dog.setId(1);
		dog.setName("dog");

		Pet pet1 = new Pet();
		pet1.setId(1);
		pet1.setName("Leo");
		pet1.setBirthDate(LocalDate.of(2020, 9, 7));
		pet1.setType(dog);

		Pet pet2 = new Pet();
		pet2.setId(2);
		pet2.setName("Basil");
		pet2.setBirthDate(LocalDate.of(2012, 8, 6));
		pet2.setType(dog);

		List<Pet> pets = Arrays.asList(pet1, pet2);
		Page<Pet> petsPage = new PageImpl<>(pets, PageRequest.of(0, 10), pets.size());
		given(this.petRepository.findAll(any(Pageable.class))).willReturn(petsPage);

		// when
		mockMvc.perform(get("/api/pets"))
			// then
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.content", hasSize(2)))
			.andExpect(jsonPath("$.content[0].id", is(1)))
			.andExpect(jsonPath("$.content[0].name", is("Leo")))
			.andExpect(jsonPath("$.content[1].id", is(2)))
			.andExpect(jsonPath("$.content[1].name", is("Basil")))
			.andExpect(jsonPath("$.pageable.pageNumber", is(0)))
			.andExpect(jsonPath("$.pageable.pageSize", is(10)))
			.andExpect(jsonPath("$.totalElements", is(2)));

		// Test with custom page and size parameters
		mockMvc.perform(get("/api/pets?page=2&size=5"))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.content", hasSize(2)));

		verify(this.petRepository, times(2)).findAll(any(Pageable.class));
	}

	@Test
	void testGetPet() throws Exception {
		// given
		PetType dog = new PetType();
		dog.setId(1);
		dog.setName("dog");

		Pet pet = new Pet();
		pet.setId(1);
		pet.setName("Leo");
		pet.setBirthDate(LocalDate.of(2020, 9, 7));
		pet.setType(dog);

		given(this.petRepository.findById(1)).willReturn(Optional.of(pet));

		// when
		mockMvc.perform(get("/api/pets/1"))
			// then
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.id", is(1)))
			.andExpect(jsonPath("$.name", is("Leo")))
			.andExpect(jsonPath("$.birthDate", is("2020-09-07")))
			.andExpect(jsonPath("$.type.id", is(1)))
			.andExpect(jsonPath("$.type.name", is("dog")));
	}

	@Test
	void testGetPetNotFound() throws Exception {
		// given
		given(this.petRepository.findById(999)).willReturn(Optional.empty());

		// when
		mockMvc.perform(get("/api/pets/999"))
			// then
			.andExpect(status().isNotFound());
	}

	@Test
	void testGetPetsByOwner() throws Exception {
		// given
		PetType dog = new PetType();
		dog.setId(1);
		dog.setName("dog");

		Pet pet1 = new Pet();
		pet1.setId(1);
		pet1.setName("Leo");
		pet1.setBirthDate(LocalDate.of(2020, 9, 7));
		pet1.setType(dog);

		Pet pet2 = new Pet();
		pet2.setId(2);
		pet2.setName("Basil");
		pet2.setBirthDate(LocalDate.of(2012, 8, 6));
		pet2.setType(dog);

		List<Pet> pets = Arrays.asList(pet1, pet2);
		Page<Pet> petsPage = new PageImpl<>(pets, PageRequest.of(0, 10), pets.size());
		given(this.ownerRepository.existsById(1)).willReturn(true);
		given(this.petRepository.findByOwnerId(eq(1), any(Pageable.class))).willReturn(petsPage);

		// when
		mockMvc.perform(get("/api/owners/1/pets"))
			// then
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.content", hasSize(2)))
			.andExpect(jsonPath("$.content[0].id", is(1)))
			.andExpect(jsonPath("$.content[0].name", is("Leo")))
			.andExpect(jsonPath("$.content[1].id", is(2)))
			.andExpect(jsonPath("$.content[1].name", is("Basil")))
			.andExpect(jsonPath("$.pageable.pageNumber", is(0)))
			.andExpect(jsonPath("$.pageable.pageSize", is(10)))
			.andExpect(jsonPath("$.totalElements", is(2)));

		// Test with custom page and size parameters
		mockMvc.perform(get("/api/owners/1/pets?page=2&size=5"))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.content", hasSize(2)));

		verify(this.petRepository, times(2)).findByOwnerId(eq(1), any(Pageable.class));
	}

	@Test
	void testGetPetsByOwnerNotFound() throws Exception {
		// given
		given(this.ownerRepository.existsById(999)).willReturn(false);

		// when
		mockMvc.perform(get("/api/owners/999/pets"))
			// then
			.andExpect(status().isNotFound());
	}

	@Test
	void testAddPet() throws Exception {
		// given
		PetType dog = new PetType();
		dog.setId(1);
		dog.setName("dog");

		Owner owner = new Owner();
		owner.setId(1);
		owner.setFirstName("George");
		owner.setLastName("Franklin");

		Pet newPet = new Pet();
		newPet.setName("Leo");
		newPet.setBirthDate(LocalDate.of(2020, 9, 7));
		newPet.setType(dog);

		given(this.ownerRepository.findById(1)).willReturn(Optional.of(owner));
		given(this.ownerRepository.save(any(Owner.class))).willAnswer(invocation -> {
			Owner savedOwner = invocation.getArgument(0);
			Pet savedPet = savedOwner.getPets().get(0);
			savedPet.setId(1);
			return savedOwner;
		});

		// when
		mockMvc
			.perform(post("/api/owners/1/pets").contentType(MediaType.APPLICATION_JSON)
				.content("{\"name\":\"Leo\",\"birthDate\":\"2020-09-07\",\"type\":{\"id\":1}}"))
			// then
			.andExpect(status().isCreated())
			.andExpect(header().string("Location", containsString("/api/owners/1/pets/1")))
			.andExpect(jsonPath("$.name", is("Leo")))
			.andExpect(jsonPath("$.birthDate", is("2020-09-07")))
			.andExpect(jsonPath("$.type.id", is(1)));

		verify(this.ownerRepository).save(any(Owner.class));
	}

	@Test
	void testAddPetOwnerNotFound() throws Exception {
		// given
		given(this.ownerRepository.findById(999)).willReturn(Optional.empty());

		// when
		mockMvc
			.perform(post("/api/owners/999/pets").contentType(MediaType.APPLICATION_JSON)
				.content("{\"name\":\"Leo\",\"birthDate\":\"2020-09-07\",\"type\":{\"id\":1}}"))
			// then
			.andExpect(status().isNotFound());

		verify(this.ownerRepository, never()).save(any(Owner.class));
	}

	@Test
	void testAddPetInvalidName() throws Exception {
		// given
		Owner owner = new Owner();
		owner.setId(1);
		owner.setFirstName("George");
		owner.setLastName("Franklin");

		given(this.ownerRepository.findById(1)).willReturn(Optional.of(owner));

		// when
		mockMvc
			.perform(post("/api/owners/1/pets").contentType(MediaType.APPLICATION_JSON)
				.content("{\"name\":\"\",\"birthDate\":\"2020-09-07\",\"type\":{\"id\":1}}"))
			// then
			.andExpect(status().isBadRequest());

		verify(this.ownerRepository, never()).save(any(Owner.class));
	}

	@Test
	void testAddPetDuplicateName() throws Exception {
		// given
		PetType dog = new PetType();
		dog.setId(1);
		dog.setName("dog");

		Pet existingPet = new Pet();
		existingPet.setId(1);
		existingPet.setName("Leo");
		existingPet.setBirthDate(LocalDate.of(2020, 9, 7));
		existingPet.setType(dog);

		// Create a spy of Owner to control the behavior of getPet method
		Owner owner = spy(new Owner());
		owner.setId(1);
		owner.setFirstName("George");
		owner.setLastName("Franklin");
		owner.addPet(existingPet);

		// Mock the behavior that causes the conflict
		// When owner.getPet("Leo", true) is called, it should return the existing pet
		doReturn(existingPet).when(owner).getPet(eq("Leo"), eq(true));

		given(this.ownerRepository.findById(1)).willReturn(Optional.of(owner));

		// when
		mockMvc
			.perform(post("/api/owners/1/pets").contentType(MediaType.APPLICATION_JSON)
				.content("{\"name\":\"Leo\",\"birthDate\":\"2020-09-07\",\"type\":{\"id\":1}}"))
			// then
			.andExpect(status().isConflict());

		verify(this.ownerRepository, never()).save(any(Owner.class));
	}

	@Test
	void testUpdatePet() throws Exception {
		// given
		PetType dog = new PetType();
		dog.setId(1);
		dog.setName("dog");

		Pet pet = new Pet();
		pet.setId(1);
		pet.setName("Leo");
		pet.setBirthDate(LocalDate.of(2020, 9, 7));
		pet.setType(dog);

		// Create a spy of Owner to control the behavior of getPet method
		Owner owner = spy(new Owner());
		owner.setId(1);
		owner.setFirstName("George");
		owner.setLastName("Franklin");

		// We need to manually add the pet to the owner's pets list
		// and make getPet(1) return the pet
		List<Pet> pets = new ArrayList<>();
		pets.add(pet);
		// Use reflection to set the pets field directly
		try {
			java.lang.reflect.Field petsField = Owner.class.getDeclaredField("pets");
			petsField.setAccessible(true);
			petsField.set(owner, pets);
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}

		// Mock the behavior of getPet(1) to return our pet
		doReturn(pet).when(owner).getPet(eq(1));

		given(this.petRepository.findById(1)).willReturn(Optional.of(pet));
		given(this.ownerRepository.findAll()).willReturn(List.of(owner));
		given(this.petTypeRepository.findById(1)).willReturn(Optional.of(dog));
		given(this.petRepository.save(any(Pet.class))).willAnswer(invocation -> {
			Pet savedPet = invocation.getArgument(0);
			savedPet.setName("Leo Updated"); // Ensure the name is updated
			return savedPet;
		});

		// when
		mockMvc
			.perform(put("/api/pets/1").contentType(MediaType.APPLICATION_JSON)
				.content("{\"name\":\"Leo Updated\",\"birthDate\":\"2020-09-07\",\"type\":{\"id\":1}}"))
			// then
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id", is(1)))
			.andExpect(jsonPath("$.name", is("Leo Updated")))
			.andExpect(jsonPath("$.birthDate", is("2020-09-07")))
			.andExpect(jsonPath("$.type.id", is(1)));

		verify(this.petRepository).save(any(Pet.class));
	}

	@Test
	void testUpdatePetNotFound() throws Exception {
		// given
		given(this.petRepository.findById(999)).willReturn(Optional.empty());

		// when
		mockMvc
			.perform(put("/api/pets/999").contentType(MediaType.APPLICATION_JSON)
				.content("{\"name\":\"Leo Updated\",\"birthDate\":\"2020-09-07\",\"type\":{\"id\":1}}"))
			// then
			.andExpect(status().isNotFound());

		verify(this.petRepository, never()).save(any(Pet.class));
	}

	@Test
	void testUpdatePetDuplicateName() throws Exception {
		// given
		PetType dog = new PetType();
		dog.setId(1);
		dog.setName("dog");

		Pet pet1 = new Pet();
		pet1.setId(1);
		pet1.setName("Leo");
		pet1.setBirthDate(LocalDate.of(2020, 9, 7));
		pet1.setType(dog);

		Pet pet2 = new Pet();
		pet2.setId(2);
		pet2.setName("Basil");
		pet2.setBirthDate(LocalDate.of(2012, 8, 6));
		pet2.setType(dog);

		// Create a spy of Owner to control the behavior of getPet methods
		Owner owner = spy(new Owner());
		owner.setId(1);
		owner.setFirstName("George");
		owner.setLastName("Franklin");

		// We need to manually add both pets to the owner's pets list
		List<Pet> pets = new ArrayList<>();
		pets.add(pet1);
		pets.add(pet2);
		// Use reflection to set the pets field directly
		try {
			java.lang.reflect.Field petsField = Owner.class.getDeclaredField("pets");
			petsField.setAccessible(true);
			petsField.set(owner, pets);
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}

		// Mock the behavior of getPet(1) to return pet1
		doReturn(pet1).when(owner).getPet(eq(1));

		// Mock the behavior of getPet("Basil", false) to return pet2
		// This is the key for the duplicate name check
		doReturn(pet2).when(owner).getPet(eq("Basil"), eq(false));

		given(this.petRepository.findById(1)).willReturn(Optional.of(pet1));
		given(this.ownerRepository.findAll()).willReturn(List.of(owner));

		// when
		mockMvc
			.perform(put("/api/pets/1").contentType(MediaType.APPLICATION_JSON)
				.content("{\"name\":\"Basil\",\"birthDate\":\"2020-09-07\",\"type\":{\"id\":1}}"))
			// then
			.andExpect(status().isConflict());

		verify(this.petRepository, never()).save(any(Pet.class));
	}

	@Test
	void testDeletePet() throws Exception {
		// given
		PetType dog = new PetType();
		dog.setId(1);
		dog.setName("dog");

		Pet pet = new Pet();
		pet.setId(1);
		pet.setName("Leo");
		pet.setBirthDate(LocalDate.of(2020, 9, 7));
		pet.setType(dog);

		// Create a spy of Owner to control the behavior of getPet method
		Owner owner = spy(new Owner());
		owner.setId(1);
		owner.setFirstName("George");
		owner.setLastName("Franklin");

		// We need to manually add the pet to the owner's pets list
		// and make getPet(1) return the pet
		List<Pet> pets = new ArrayList<>();
		pets.add(pet);
		// Use reflection to set the pets field directly
		try {
			java.lang.reflect.Field petsField = Owner.class.getDeclaredField("pets");
			petsField.setAccessible(true);
			petsField.set(owner, pets);
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}

		// Mock the behavior of getPet(1) to return our pet
		doReturn(pet).when(owner).getPet(eq(1));

		given(this.petRepository.existsById(1)).willReturn(true);
		given(this.ownerRepository.findAll()).willReturn(List.of(owner));

		// Mock the save method to return the owner
		given(this.ownerRepository.save(any(Owner.class))).willReturn(owner);

		// when
		mockMvc.perform(delete("/api/pets/1"))
			// then
			.andExpect(status().isNoContent());

		verify(this.petRepository).deleteById(1);
		verify(this.ownerRepository).save(any(Owner.class));
	}

	@Test
	void testDeletePetNotFound() throws Exception {
		// given
		given(this.petRepository.existsById(999)).willReturn(false);

		// when
		mockMvc.perform(delete("/api/pets/999"))
			// then
			.andExpect(status().isNotFound());

		verify(this.petRepository, never()).deleteById(anyInt());
		verify(this.ownerRepository, never()).save(any(Owner.class));
	}

}
