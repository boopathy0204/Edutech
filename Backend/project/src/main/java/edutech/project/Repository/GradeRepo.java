package edutech.project.Repository;

import edutech.project.model.Grade;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GradeRepo extends JpaRepository<Grade,Long> {
}
