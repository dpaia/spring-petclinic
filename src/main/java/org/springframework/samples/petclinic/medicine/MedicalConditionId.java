package org.springframework.samples.petclinic.medicine;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.util.Objects;

@Embeddable
public class MedicalConditionId {

	@Column(name = "condition_code")
	private String code;

	@Column(name = "locale")
	private String locale;

	public MedicalConditionId() {
	}

	public MedicalConditionId(String code, String locale) {
		this.code = code;
		this.locale = locale;
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

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		MedicalConditionId that = (MedicalConditionId) o;
		return Objects.equals(code, that.code) && Objects.equals(locale, that.locale);
	}

	@Override
	public int hashCode() {
		return Objects.hash(code, locale);
	}

}
