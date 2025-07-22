package org.springframework.samples.petclinic.medication;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class MedicationRepositoryTest {

	@Autowired
	private MedicationRepository medicationRepository;

	@Test
	@Transactional(Transactional.TxType.NEVER)
	void testMedicationIdGenerated() {
		Medication medication = new Medication();
		Medication saved = medicationRepository.save(medication);
		assertNotNull(saved.getId());

		Optional<Medication> found = medicationRepository.findById(saved.getId());
		assertTrue(found.isPresent());
	}
}
