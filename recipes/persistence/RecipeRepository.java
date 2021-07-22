package recipes.persistence;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

import recipes.businessLayer.Recipe;

@Repository
public interface RecipeRepository extends CrudRepository<Recipe, Integer> {
    Recipe findRecipeById(Integer id);
    List<Recipe> findByNameIgnoreCaseContaining(String name);
    List<Recipe> findByCategoryIgnoreCase(String category);
}
