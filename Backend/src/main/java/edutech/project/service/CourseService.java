package edutech.project.service;

import edutech.project.dto.request.CourseRequestDTO;
import edutech.project.dto.response.CourseResponseDTO;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public interface CourseService {
    CourseResponseDTO createCourse(CourseRequestDTO request);
    CourseResponseDTO updateCourse(Long courseId, CourseRequestDTO request);
    void deleteCourse(Long courseId);
    CourseResponseDTO getCourseById(Long courseId);
    List<CourseResponseDTO> getAllCourses(String query, Long academicPeriodId);
    List<CourseResponseDTO> getCoursesByProfessor(Long professorId, String query, Long academicPeriodId);
    void completeCourse(Long courseId);

}
