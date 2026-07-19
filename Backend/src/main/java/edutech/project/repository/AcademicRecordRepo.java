package edutech.project.repository;

import edutech.project.model.AcademicRecord;
import edutech.project.model.Course;
import edutech.project.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AcademicRecordRepo extends JpaRepository<AcademicRecord,Long> {
    List<AcademicRecord> findByStudent(Student student);
    boolean existsByStudentAndCourse(Student student, Course course);
    boolean existsByStudentAndCourse_CourseCodeAndFinalGradeNot(Student student, String courseCode, String finalGrade);
}
