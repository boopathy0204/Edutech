package edutech.project.repository;

import edutech.project.model.Student;
import edutech.project.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface StudentRepo extends JpaRepository<Student,Long> {
    boolean existsByRegistrationNumber(String registrationNumber);
    boolean existsByRegistrationNumberAndStudentIdNot(String registrationNumber, Long studentId);
    boolean existsByUser(User user);
    boolean existsByUserAndStudentIdNot(User user, Long studentId);
    Optional<Student> findByUser(User user);
}
