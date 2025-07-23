package org.springframework.samples.petclinic.owner;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public record Prescription(@Column(name = "medicine") String medicine,

		@Column(name = "notes") String notes) {
}
