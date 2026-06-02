package org.springframework.samples.petclinic.medication;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.Repository;

import java.util.UUID;

public interface MedicationRepository extends CrudRepository<Medication, UUID> {
}
