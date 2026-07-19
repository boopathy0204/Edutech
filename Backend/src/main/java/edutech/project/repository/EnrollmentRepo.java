package edutech.project.repository;

import edutech.project.model.Course;
import edutech.project.model.Enrollment;
import edutech.project.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EnrollmentRepo extends JpaRepository<Enrollment,Long> {
    boolean existsByStudentAndCourse(Student student, Course course);
    List<Enrollment> findByStudent(Student student);
    List<Enrollment> findByCourse(Course course);
}
