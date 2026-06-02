package org.springframework.samples.petclinic.owner;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class OwnerRepositoryTest {

	@Autowired
	private OwnerRepository owners;

	@Test
	void testSaveOwnerAddress() {
		Owner owner = createOwner();
		owner.setAddress(new Address("123 Main Street", "Bedrock"));
		Integer id = owners.save(owner).getId();

		Owner savedOwner1 = owners.findById(id).orElse(null);
		assertNotNull(savedOwner1);
		assertNotNull(savedOwner1.getAddress());
		assertEquals("123 Main Street", savedOwner1.getAddress().address());
		assertEquals("Bedrock", savedOwner1.getAddress().city());

		savedOwner1.setAddress(new Address("456 Second Address", "New Bedrock"));
		owners.save(owner);

		Owner savedOwner2 = owners.findById(id).orElse(null);
		assertNotNull(savedOwner2);
		assertEquals("456 Second Address", savedOwner2.getAddress().address());
		assertEquals("New Bedrock", savedOwner2.getAddress().city());
	}

	@Test
	void testFindByOwnerAddress() {
		Owner owner = createOwner();
		owner.setAddress(new Address("123 Main Street", "Bedrock"));
		owners.save(owner);

		List<Owner> ownerList = owners.findAllByAddressCity("Bedrock");
		assertFalse(ownerList.isEmpty());
	}

	private Owner createOwner() {
		Owner owner = new Owner();
		owner.setFirstName("George");
		owner.setLastName("Franklin");
		owner.setTelephone("6085551023");
		return owner;
	}



}
