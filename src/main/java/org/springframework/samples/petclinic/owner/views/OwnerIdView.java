/*
 * Projection for lightweight Owner search results to avoid fetching pets/visits.
 */
package org.springframework.samples.petclinic.owner.views;

/**
 * Java record used as a JPA projection to fetch only Owner id.
 */
public record OwnerIdView(
		Integer id
) {
}
