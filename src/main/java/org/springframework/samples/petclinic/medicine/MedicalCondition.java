package org.springframework.samples.petclinic.medicine;

import jakarta.persistence.*;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "medical_condition")
@IdClass(MedicalConditionId.class)
public class MedicalCondition {

	@Id
	@Column(name = "condition_code")
	private String code;

	@Id
	@Column(name = "locale")
	private String locale;

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

	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

}
