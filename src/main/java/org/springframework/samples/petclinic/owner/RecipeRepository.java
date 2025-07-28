package org.springframework.samples.petclinic.owner;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {

	List<Recipe> findAllByVisitId(int visitId);

}
