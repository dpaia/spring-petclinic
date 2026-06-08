package org.springframework.samples.petclinic.medication;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class MedicationUuidIntegrationTests {

	@Autowired
	private MedicationRepository medications;

	@Test
	void shouldGenerateUuidOnSaveAndRetrieveById() {
		Medication m = new Medication();
		m.setName("Aspirin");

		Medication saved = medications.save(m);
		assertThat(saved.getId()).as("UUID should be generated on save").isNotNull();

		UUID id = saved.getId();
		Optional<Medication> found = medications.findById(id);
		assertThat(found).isPresent();
		assertThat(found.get().getId()).isEqualTo(id);
		assertThat(found.get().getName()).isEqualTo("Aspirin");

		// UUID string format check (36 chars with hyphens)
		assertThat(id.toString()).hasSize(36);
		assertThat(id.toString()).contains("-");
	}

	@Test
	void eachEntityShouldHaveUniqueUuid() {
		Medication m1 = new Medication();
		m1.setName("Ibuprofen");
		Medication m2 = new Medication();
		m2.setName("Paracetamol");
		Medication m3 = new Medication();
		m3.setName("Paracetamol");

		Medication s1 = medications.save(m1);
		Medication s2 = medications.save(m2);
		Medication s3 = medications.save(m3);

		assertThat(s1.getId()).isNotNull();
		assertThat(s2.getId()).isNotNull();
		assertThat(s3.getId()).isNotNull();
		assertThat(s1.getId()).isNotEqualTo(s2.getId());
		assertThat(s1.getId()).isNotEqualTo(s3.getId());
		assertThat(s2.getId()).isNotEqualTo(s3.getId());
	}
}
