package org.springframework.samples.petclinic.owner;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;

@Embeddable
public class Address {

	@Column(name = "address")
	@NotBlank
	private String address;

	@Column(name = "city")
	@NotBlank
	private String city;

	protected Address() {
	}

	public Address(String address, String city) {
		this.address = address;
		this.city = city;
	}

	public String address() {
		return this.address;
	}

	public String getAddress() {
		return this.address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String city() {
		return this.city;
	}

	public String getCity() {
		return this.city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof Address address)) {
			return false;
		}
		return java.util.Objects.equals(this.address, address.address)
				&& java.util.Objects.equals(this.city, address.city);
	}

	@Override
	public int hashCode() {
		return java.util.Objects.hash(this.address, this.city);
	}

	@Override
	public String toString() {
		return this.address;
	}

}
