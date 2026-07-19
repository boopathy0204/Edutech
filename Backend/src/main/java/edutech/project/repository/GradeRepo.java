package edutech.project.repository;

import edutech.project.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface GradeRepo extends JpaRepository<Grade,Long> {
    boolean existsBySubmission(Submission submission);
    List<Grade> findBySubmission_Student(Student student);
    List<Grade> findBySubmission_Assignment(Assignment assignment);
    List<Grade> findBySubmission_StudentAndSubmission_Assignment_Course(Student student, Course course);
    Optional<Grade> findBySubmission(Submission submission);
    @Query("""
            SELECT g FROM Grade g 
            JOIN FETCH g.submission s 
            JOIN FETCH s.student st 
            JOIN FETCH s.assignment a 
            JOIN FETCH a.course c 
            WHERE c.professor.professorId = :professorId 
            AND (:courseId IS NULL OR c.courseId = :courseId) 
            AND (:query IS NULL OR :query = ''
                 OR LOWER(st.firstName) LIKE LOWER(CONCAT('%', :query, '%')) 
                 OR LOWER(st.lastName) LIKE LOWER(CONCAT('%', :query, '%')) 
                 OR LOWER(a.title) LIKE LOWER(CONCAT('%', :query, '%'))
                 ) 
            ORDER BY c.courseId, a.assignmentId, st.firstName""")

    List<Grade> findGradeCenterByProfessor(@Param("professorId") Long professorId,@Param("courseId") Long courseId,@Param("query") String query);

}
