package edutech.project.repository;

import edutech.project.model.Professor;
import edutech.project.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ProfessorRepo extends JpaRepository<Professor,Long> {
    boolean existsByEmployeeCode(String employeeCode);
    boolean existsByEmployeeCodeAndProfessorIdNot(String employeeCode,Long professorId);
    boolean existsByUser(User user);
    boolean existsByUserAndProfessorIdNot(User user, Long professorId);
    Optional<Professor> findByUser(User user);
}
