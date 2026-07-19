package edutech.project.Repository;

import edutech.project.model.Submission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubmissionRepo extends JpaRepository<Submission,Long> {
}
