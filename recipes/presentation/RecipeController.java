package recipes.presentation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import recipes.businessLayer.Recipe;
import recipes.businessLayer.RecipeService;
import recipes.businessLayer.User;
import recipes.businessLayer.UserService;

@RestController
@Validated
public class RecipeController {

    private final RecipeService recipeService;
    private final UserService userService;

    @Autowired
    public RecipeController(RecipeService recipeService, UserService userService) {
        this.recipeService = recipeService;
        this.userService = userService;
    }

    @PostMapping("/api/register")
    public ResponseEntity<String> registerUser(@Valid @RequestBody User user) {
        if (userService.registerUser(user)) {
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/api/recipe/new")
    public ResponseEntity<String> setRecipeToStorage(@Valid @RequestBody Recipe body) {
        body.setDate(LocalDateTime.now());
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        body.setUserEmail(email);
        int recipeIndex = recipeService.addNewRecipe(body);
        String response = String.format("{id: %d}", recipeIndex);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/api/recipe/{id}")
    public ResponseEntity<Recipe> getRecipeFromStorage(@PathVariable int id) {
        Recipe neededRecipe = recipeService.findRecipeById(id);
        if (neededRecipe == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        return new ResponseEntity<>(
                neededRecipe, headers, HttpStatus.OK
        );
    }

    @GetMapping(value = "/api/recipe/search/", params = "name")
    public ResponseEntity<List<Recipe>> getRecipesByName(@RequestParam String name) {
        List<Recipe> recipes = recipeService.findRecipesByName(name);
        HttpHeaders headers = new HttpHeaders();
        if (recipes.size() > 0) {
            headers.set("Content-Type", "application/json");
            return new ResponseEntity<>(
                    recipes, headers, HttpStatus.OK
            );
        }
        return new ResponseEntity<>(
                new ArrayList<>(), headers, HttpStatus.OK
        );
    }

    @GetMapping(value = "/api/recipe/search/", params = "category")
    public ResponseEntity<List<Recipe>> getRecipesByrCategory(@RequestParam String category) {
        List<Recipe> recipes = recipeService.findRecipesByCategory(category);
        HttpHeaders headers = new HttpHeaders();
        if (recipes.size() > 0) {
            headers.set("Content-Type", "application/json");
            return new ResponseEntity<>(
                    recipes, headers, HttpStatus.OK
            );
        }
        return new ResponseEntity<>(
                new ArrayList<>(), headers, HttpStatus.OK
        );
    }

    @DeleteMapping("/api/recipe/{id}")
    public ResponseEntity<Integer> deletePost(@PathVariable Integer id) {
        int removeStatus = recipeService.deleteRecipe(id);
        if (removeStatus == 3) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else if (removeStatus == 2) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/api/recipe/{id}")
    public ResponseEntity<Integer> updateRecipe(@PathVariable int id, @Valid @RequestBody Recipe recipe) {
        int updateStatus = recipeService.updateRecipe(id, recipe);
        if (updateStatus == 1) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else if (updateStatus == 2) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }
}
