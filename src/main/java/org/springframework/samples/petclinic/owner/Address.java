package org.springframework.samples.petclinic.owner;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;

@Embeddable
public record Address(

		@Column(name = "address") @NotBlank String address,

		@Column(name = "city") @NotBlank String city

) {
}
