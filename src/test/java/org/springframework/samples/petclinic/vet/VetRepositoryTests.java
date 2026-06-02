package org.springframework.samples.petclinic.vet;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class VetRepositoryTests {

	@Autowired
	private VetRepository vetRepository;

	@Test
	void testFindAllSpecialtyNames() {
		Page<String> result = vetRepository.findAllSpecialtyNames(2, PageRequest.of(0, 10));
		assertEquals(1, result.getTotalElements());
		assertEquals("radiology", result.getContent().get(0));

		result = vetRepository.findAllSpecialtyNames(3, PageRequest.of(1, 1));
		assertEquals(2, result.getTotalElements());
		assertEquals(2, result.getTotalPages());
		assertEquals("dentistry", result.getContent().get(0));
	}

}
