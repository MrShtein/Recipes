package recipes.persistence;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

import recipes.businessLayer.User;

public interface UserRepository extends CrudRepository<User, Integer> {
    Optional<User> findByEmail(String email);
}
