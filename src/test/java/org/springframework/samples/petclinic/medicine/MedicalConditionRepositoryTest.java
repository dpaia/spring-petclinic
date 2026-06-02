package org.springframework.samples.petclinic.medicine;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.samples.petclinic.owner.OwnerRepository;
import org.springframework.samples.petclinic.owner.Visit;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class MedicalConditionRepositoryTest {

	@Autowired
	private MedicalConditionRepository medicalConditionRepository;

	@Autowired
	private OwnerRepository ownerRepository;

	@Test
	@Transactional(Transactional.TxType.NEVER)
	void testSaveMedicalCondition() {
		MedicalCondition medicalCondition = createMedicalCondition();
		medicalCondition.setNames(Set.of("A1", "B2"));

		medicalConditionRepository.save(medicalCondition);
		Optional<MedicalCondition> found = medicalConditionRepository
			.findById(medicalCondition.getMedicalConditionId());
		assertTrue(found.isPresent());
		assertEquals(medicalCondition.getNames(), found.get().getNames());
	}

	@Test
	@Transactional(Transactional.TxType.NEVER)
	void testReferenceToMedicalCondition() {
		MedicalCondition medicalCondition = medicalConditionRepository.save(createMedicalCondition());
		Visit visit = new Visit();
		visit.setDescription("Test visit");
		visit.setMedicalCondition(medicalCondition);

		Owner owner = ownerRepository.findById(1).get();
		owner.getPet(1).addVisit(visit);
		ownerRepository.save(owner);

		owner = ownerRepository.findById(1).get();
		visit = owner.getPet(1).getVisits().iterator().next();
		assertEquals(medicalCondition.getMedicalConditionId(), visit.getMedicalCondition().getMedicalConditionId());
	}

	private MedicalCondition createMedicalCondition() {
		MedicalConditionId medicalConditionId = new MedicalConditionId();
		medicalConditionId.setCode("111");
		medicalConditionId.setLocale("aaa");
		MedicalCondition medicalCondition = new MedicalCondition();
		medicalCondition.setMedicalConditionId(medicalConditionId);
		return medicalCondition;
	}

}
