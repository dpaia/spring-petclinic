package org.springframework.samples.petclinic.medicine;

import org.springframework.data.repository.CrudRepository;

public interface MedicalConditionRepository extends CrudRepository<MedicalCondition, MedicalConditionId> {

}
