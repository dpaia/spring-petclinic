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
		MedicalCondition medicalCondition = new MedicalCondition();
		medicalCondition.setCode("111");
		medicalCondition.setLocale("aaa");
		medicalCondition.setNames(Set.of("A1", "B2"));

		medicalConditionRepository.save(medicalCondition);
		Optional<MedicalCondition> found = medicalConditionRepository.findById(new MedicalConditionId("111", "aaa"));
		assertTrue(found.isPresent());
		assertEquals(medicalCondition.getNames(), found.get().getNames());
	}

	@Test
	@Transactional(Transactional.TxType.NEVER)
	void testReferenceToMedicalCondition() {
		MedicalCondition medicalCondition = new MedicalCondition();
		medicalCondition.setCode("111");
		medicalCondition.setLocale("aaa");
		Visit visit = new Visit();
		visit.setDescription("Test visit");
		visit.setMedicalCondition(medicalCondition);

		Owner owner = ownerRepository.findById(1).get();
		owner.getPet(1).addVisit(visit);
		ownerRepository.save(owner);

		owner = ownerRepository.findById(1).get();
		visit = owner.getPet(1).getVisits().iterator().next();
		assertEquals(medicalCondition.getCode(), visit.getMedicalCondition().getCode());
		assertEquals(medicalCondition.getLocale(), visit.getMedicalCondition().getLocale());
	}

}
