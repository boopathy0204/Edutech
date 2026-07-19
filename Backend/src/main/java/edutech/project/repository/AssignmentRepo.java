package edutech.project.repository;

import edutech.project.model.Assignment;
import edutech.project.model.Course;
import edutech.project.model.Grade;
import edutech.project.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AssignmentRepo extends JpaRepository<Assignment,Long> {
    List<Assignment> findByCourse(Course course);
    boolean existsByCourseAndTitle(Course course, String title);
    boolean existsByCourseAndTitleAndAssignmentIdNot(Course course ,String title,Long assingmentId);
    @Query("""
            SELECT  a FROM Assignment a 
            JOIN FETCH a.course c 
            WHERE c.professor.professorId = :professorId 
            AND (:courseId IS NULL OR c.courseId = :courseId) 
            AND (:academicPeriodId IS NULL OR c.academicPeriod.academicPeriodId = :academicPeriodId)
            AND (:query IS NULL OR :query = ''
                 OR LOWER(a.title) LIKE LOWER(CONCAT('%', :query, '%'))
                 OR LOWER(c.courseCode) LIKE LOWER(CONCAT('%', :query, '%'))
                 )  
            """)
    List<Assignment> findAssignmentByProfessor(@Param("professorId") Long professorId, @Param("courseId") Long courseId, @Param("query") String query, @Param("academicPeriodId") Long academicPeriodId);

}
