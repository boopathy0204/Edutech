package edutech.project.repository;

import edutech.project.model.AcademicPeriod;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

public interface AcademicPeriodRepo extends JpaRepository<AcademicPeriod, Long> {
    Optional<AcademicPeriod> findByAcademicYearAndAcademicHalf(String academicYear, String academicHalf);
    Optional<AcademicPeriod> findFirstByStatus(String status);
    List<AcademicPeriod> findByStatus(String status);
}
