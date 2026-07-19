package edutech.project.repository;

import edutech.project.model.Course;
import edutech.project.model.DayOfWeek;
import edutech.project.model.Professor;
import edutech.project.model.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ScheduleRepo extends JpaRepository<Schedule,Long> {
    List<Schedule> findByCourse(Course course);
    List<Schedule> findByDayAndRoomNumber(DayOfWeek day, String roomNumber);
    List<Schedule> findByDayAndCourse_Professor(DayOfWeek day, Professor professor);
    @Query("""
            SELECT s FROM Schedule s 
            JOIN FETCH s.course c 
            JOIN Enrollment e ON e.course.courseId = c.courseId 
            WHERE e.student.studentId = :studentId
            """)
    List<Schedule> findScheduleByStudentId(@Param("studentId") Long studentId);
}
