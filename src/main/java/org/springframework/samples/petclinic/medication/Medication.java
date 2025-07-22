package org.springframework.samples.petclinic.medication;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "medication")
public class Medication {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "id", nullable = false)
	private UUID id;

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

}
