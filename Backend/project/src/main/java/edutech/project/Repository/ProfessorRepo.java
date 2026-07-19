package edutech.project.Repository;

import edutech.project.model.Professor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfessorRepo extends JpaRepository<Professor,Long> {
}
