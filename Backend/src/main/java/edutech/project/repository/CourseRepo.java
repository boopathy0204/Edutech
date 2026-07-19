package edutech.project.repository;

import edutech.project.model.Course;
import edutech.project.model.Professor;
import edutech.project.model.AcademicPeriod;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseRepo extends JpaRepository<Course,Long> {
    List<Course> findByProfessor(Professor professor);
    boolean existsByCourseCode(String courseCode);
    boolean existsByCourseCodeAndCourseIdNot(String courseCode, Long courseId);
    
    boolean existsByCourseCodeAndAcademicPeriod(String courseCode, AcademicPeriod academicPeriod);
    boolean existsByCourseCodeAndAcademicPeriodAndCourseIdNot(String courseCode, AcademicPeriod academicPeriod, Long courseId);
    
    List<Course> findByAcademicPeriod(AcademicPeriod academicPeriod);
    List<Course> findByAcademicPeriodAndCourseStatus(AcademicPeriod academicPeriod, String courseStatus);
    List<Course> findByAcademicPeriodAndProfessor(AcademicPeriod academicPeriod, Professor professor);
    
    List<Course> findByCourseNameContainingIgnoreCaseOrCourseCodeContainingIgnoreCase(String courseName, String courseCode);
    
    List<Course> findByProfessor_ProfessorIdAndCourseNameContainingIgnoreCaseOrProfessor_ProfessorIdAndCourseCodeContainingIgnoreCase(
            Long professorId1, String courseName, Long professorId2, String courseCode);
    
    List<Course> findByAcademicPeriodAndProfessor_ProfessorIdAndCourseNameContainingIgnoreCaseOrAcademicPeriodAndProfessor_ProfessorIdAndCourseCodeContainingIgnoreCase(
            AcademicPeriod academicPeriod1, Long professorId1, String courseName, AcademicPeriod academicPeriod2, Long professorId2, String courseCode);
}
