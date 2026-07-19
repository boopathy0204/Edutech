package edutech.project.Repository;

import edutech.project.model.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssignmentRepo extends JpaRepository<Assignment,Long> {
}
