package org.springframework.samples.petclinic.owner;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class RecipeTest {

	@Autowired
	private VisitRepository visitRepository;

	@Autowired
	private RecipeRepository recipeRepository;

	@Test
	@Transactional(Transactional.TxType.NEVER)
	void testSaveVisitWithRecipe() {
		Visit visit = visitRepository.findById(1).get();
		Recipe recipe = new Recipe();
		recipe.setText("Test");
		recipe.setVisit(visit);

		recipeRepository.save(recipe);
		List<Recipe> recipes = recipeRepository.findAllByVisitId(visit.getId());
		assertEquals(1, recipes.size());
		assertEquals("Test", recipes.iterator().next().getText());

		visitRepository.delete(visitRepository.findById(1).get());
		recipes = recipeRepository.findAllByVisitId(visit.getId());
		assertEquals(0, recipes.size());
	}

}
