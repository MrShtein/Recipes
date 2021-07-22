package recipes.businessLayer;

import com.fasterxml.jackson.annotation.OptBoolean;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import recipes.persistence.RecipeRepository;

@Service
public class RecipeService {

    private final RecipeRepository recipeRepository;

    @Autowired
    public RecipeService(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    public Recipe findRecipeById(Integer id) {
        return recipeRepository.findRecipeById(id);
    }

    public int addNewRecipe(Recipe recipe) {
        return recipeRepository.save(recipe).getId();
    }

    public int deleteRecipe(Integer id) {
        Recipe recipe = recipeRepository.findRecipeById(id);
        if (recipe == null) {
            return 3;
        }
        if (compareAuthorToUser(recipe.getUserEmail())) {
            recipeRepository.deleteById(id);
            return 1;
        } else {
            return 2;
        }
    }

    public int updateRecipe(int id, Recipe recipe) {
        Recipe oldRecipe = recipeRepository.findRecipeById(id);
        if (oldRecipe == null) {
            return 2;
        }
        if (compareAuthorToUser(oldRecipe.getUserEmail())) {
            recipe.setId(oldRecipe.getId());
            recipe.setDate(LocalDateTime.now());
            recipe.setUserEmail(oldRecipe.getUserEmail());
            recipeRepository.save(recipe);
            return 1;
        }
        return 3;
    }

    public List<Recipe> findRecipesByName(String name) {
        List<Recipe> recipes = recipeRepository.findByNameIgnoreCaseContaining(name);
        return sortRecipesByDate(recipes);
    }

    public List<Recipe> findRecipesByCategory(String category) {
        List<Recipe> recipes = recipeRepository.findByCategoryIgnoreCase(category);
        return sortRecipesByDate(recipes);
    }

    private List<Recipe> sortRecipesByDate(List<Recipe> recipes) {
       recipes.sort(new Comparator<Recipe>() {
            @Override
            public int compare(Recipe recipe, Recipe t1) {
                return t1.getDate().compareTo(recipe.getDate());
            }
        });
       return recipes;
    }

    private boolean compareAuthorToUser(String email) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return email.equals(authentication.getName());
    }
}
