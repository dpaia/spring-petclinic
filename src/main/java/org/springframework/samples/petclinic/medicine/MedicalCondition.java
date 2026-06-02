package org.springframework.samples.petclinic.medicine;

import jakarta.persistence.*;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "medical_condition")
public class MedicalCondition {

	@EmbeddedId
	private MedicalConditionId medicalConditionId;

	@ElementCollection(fetch = FetchType.EAGER)
	@Column(name = "name")
	@CollectionTable(name = "medical_condition_names",
			joinColumns = { @JoinColumn(name = "medical_condition_code", referencedColumnName = "condition_code"),
					@JoinColumn(name = "medical_condition_locale", referencedColumnName = "locale") })
	private Set<String> names = new LinkedHashSet<>();

	public Set<String> getNames() {
		return names;
	}

	public void setNames(Set<String> names) {
		this.names = names;
	}

	public MedicalConditionId getMedicalConditionId() {
		return medicalConditionId;
	}

	public void setMedicalConditionId(MedicalConditionId medicalConditionId) {
		this.medicalConditionId = medicalConditionId;
	}

}
