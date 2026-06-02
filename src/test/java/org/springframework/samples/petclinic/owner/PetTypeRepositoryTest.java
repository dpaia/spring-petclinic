package org.springframework.samples.petclinic.owner;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class PetTypeRepositoryTest {

	@Autowired
	private PetTypeRepository petTypeRepository;

	@Test
	@Transactional(Transactional.TxType.NEVER)
	void testVersionUpdated() {
		PetType petType = new PetType();
		petType.setName("Aaa");
		petType = petTypeRepository.save(petType);

		Integer firstVersion = petType.getVersion();
		assertNotNull(firstVersion);

		petType.setName("Bbb");
		petType = petTypeRepository.save(petType);
		assertTrue(petType.getVersion() > firstVersion);
	}

	@Test
	@Transactional(Transactional.TxType.NEVER)
	void testConcurrentModification() {
		PetType petType = new PetType();
		petType.setName("Aaa");
		PetType petType1 = petTypeRepository.save(petType);
		petType1.setName("Bbb");

		PetType petType2 = petTypeRepository.findPetTypes()
			.stream()
			.filter(it -> it.getName().equals("Aaa"))
			.findFirst()
			.get();
		petType2.setName("Ccc");

		petTypeRepository.save(petType2);
		assertThrows(ObjectOptimisticLockingFailureException.class, () -> {
			petTypeRepository.save(petType1);
		});
	}

}
