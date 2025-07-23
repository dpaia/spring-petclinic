package org.springframework.samples.petclinic.medication;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

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

	@ParameterizedTest
	@ValueSource(strings = { "b5f3db68-65b6-4dc6-bfd3-f5925f3f5eff", "427f69d5-7089-4cf0-b1f9-74d41811388a",
			"40b9a15f-8e61-4d13-be1c-d06fa08ea8cb" })
	@Transactional(Transactional.TxType.NEVER)
	void testSeveralUuidsGenerated(String uuidString) {
		UUID uuid = UUID.fromString(uuidString);
		try (MockedStatic<UUID> uuidMockedStatic = Mockito.mockStatic(UUID.class)) {
			uuidMockedStatic.when(UUID::randomUUID).thenReturn(uuid);
			Medication medication = new Medication();
			Medication saved = medicationRepository.save(medication);
			assertEquals(uuid, saved.getId());

			Optional<Medication> found = medicationRepository.findById(uuid);
			assertTrue(found.isPresent());
		}
		finally {
			Mockito.framework().clearInlineMocks();
		}
	}

}
