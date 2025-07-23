package org.springframework.samples.petclinic.owner;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class VisitTest {

	@Autowired
	private OwnerRepository ownerRepository;

	@Test
	@Transactional(Transactional.TxType.NEVER)
	void testVisitPrescriptions() {
		Owner owner = ownerRepository.findById(6).get();
		Visit visit = owner.getPet(7).getVisits().iterator().next();
		visit.getPrescription().add(new Prescription("Aaa", "111"));
		visit.getPrescription().add(new Prescription("Bbb", "222"));
		ownerRepository.save(owner);

		owner = ownerRepository.findById(6).get();
		visit = owner.getPet(7).getVisits().iterator().next();
		assertEquals(2, visit.getPrescription().size());
		assertTrue(visit.getPrescription().contains(new Prescription("Aaa", "111")));
		assertTrue(visit.getPrescription().contains(new Prescription("Bbb", "222")));

		visit.setPrescription(
				Set.of(new Prescription("Aaa", "001"), new Prescription("Bbb", "222"), new Prescription("Ccc", "333")));
		ownerRepository.save(owner);

		owner = ownerRepository.findById(6).get();
		visit = owner.getPet(7).getVisits().iterator().next();
		assertEquals(3, visit.getPrescription().size());
		assertTrue(visit.getPrescription().contains(new Prescription("Aaa", "001")));
		assertTrue(visit.getPrescription().contains(new Prescription("Bbb", "222")));
		assertTrue(visit.getPrescription().contains(new Prescription("Ccc", "333")));
	}

}
