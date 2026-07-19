package edutech.project.repository;

import edutech.project.model.Student;
import edutech.project.model.Submission;
import edutech.project.model.Assignment;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SubmissionRepo extends JpaRepository<Submission,Long> {
    boolean existsByStudentAndAssignment(Student student, Assignment assignment);
    List<Submission> findByStudent(Student student);
    List<Submission> findByAssignment(Assignment assignment);
    Optional<Submission> findByStudentAndAssignment(Student student, Assignment assignment);
    @Query("""
            SELECT s FROM Submission s
            JOIN FETCH s.student st
            JOIN FETCH s.assignment a
            WHERE a.assignmentId = :assignmentId
            AND (:status IS NULL OR :status = '' OR LOWER(s.status) LIKE LOWER(CONCAT('%', :status, '%')))
            AND (:query IS NULL 
                 OR :query = '' 
                 OR LOWER(st.firstName) LIKE LOWER(CONCAT('%', :query, '%')) 
                 OR LOWER(st.lastName) LIKE LOWER(CONCAT('%', :query, '%')) 
                 OR LOWER(st.registrationNumber) LIKE LOWER(CONCAT('%', :query, '%')))
            ORDER BY s.submittedAt DESC""")
    List<Submission> findSubmissionsByAssignmentWithFilters(
            @Param("assignmentId") Long assignmentId,
            @Param("status") String status,
            @Param("query") String query);
    @Query("""
            SELECT s FROM Submission s
            JOIN FETCH s.student st 
            JOIN FETCH s.assignment a 
            JOIN FETCH a.course c 
            WHERE c.professor.professorId = :professorId 
            AND (:courseId IS NULL OR c.courseId = :courseId) 
            AND (:academicPeriodId IS NULL OR c.academicPeriod.academicPeriodId = :academicPeriodId)
            AND(    :status IS NULL OR :status = ''
                OR LOWER(s.status) LIKE LOWER(CONCAT('%',:status,'%'))
                )
            AND (:query IS NULL 
                 OR :query = '' 
                 OR LOWER(st.firstName) LIKE LOWER(CONCAT('%', :query, '%')) 
                 OR LOWER(st.lastName) LIKE LOWER(CONCAT('%', :query, '%')) 
                 OR LOWER(st.registrationNumber) LIKE LOWER(CONCAT('%', :query, '%')) 
                 OR LOWER(a.title) LIKE LOWER(CONCAT('%', :query, '%'))
                 OR LOWER(c.courseCode) LIKE LOWER(CONCAT('%', :query, '%'))) 
            ORDER BY s.submittedAt DESC""")
    List<Submission> findSubmissionCenterByProfessor(
            @Param("professorId") Long professorId,
            @Param("courseId") Long courseId,
            @Param("status") String status,
            @Param("query") String query,
            @Param("academicPeriodId") Long academicPeriodId);
}
