package edutech.project.repository;

import edutech.project.model.Course;
import edutech.project.model.CourseMaterial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
public interface CourseMaterialRepo extends JpaRepository<CourseMaterial,Long> {
    List<CourseMaterial> findByCourse(Course course);
    @Query("""
            SELECT cm FROM CourseMaterial cm 
            JOIN FETCH cm.course c 
            WHERE c.professor.professorId = :professorId 
            AND (:courseId IS NULL OR c.courseId = :courseId) 
            AND (:academicPeriodId IS NULL OR c.academicPeriod.academicPeriodId = :academicPeriodId)
            AND (:query IS NULL OR :query = ''
                 OR LOWER(cm.title) LIKE LOWER(CONCAT('%', :query, '%'))
                 OR LOWER(c.courseCode) LIKE LOWER(CONCAT('%', :query, '%'))
                 )  
            """)
    List<CourseMaterial> findMaterialByProfessor(@Param("professorId") Long professorId, @Param("courseId") Long courseId, @Param("query") String query, @Param("academicPeriodId") Long academicPeriodId);
}
