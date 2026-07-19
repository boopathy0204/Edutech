package edutech.project.repository;

import edutech.project.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepo extends JpaRepository<User,Long> {
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByUsernameAndUserIdNot(String username, Long userId);
    boolean existsByEmailAndUserIdNot(String email, Long userId);
    Optional<User> findByUsername(String username);
}
