package edutech.project.Repository;

import edutech.project.model.AcademicRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AcademicRecordRepo extends JpaRepository<AcademicRecord,Long> {
}
