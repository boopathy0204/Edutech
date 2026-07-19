package edutech.project.service;

import edutech.project.dto.request.EnrollmentRequestDTO;
import edutech.project.dto.response.CourseStudentDTO;
import edutech.project.dto.response.EnrollmentResponseDTO;
import edutech.project.dto.response.StudentCourseDTO;

import java.util.List;

public interface EnrollmentService {

    EnrollmentResponseDTO createEnrollment(EnrollmentRequestDTO request);
    EnrollmentResponseDTO getEnrollmentById(Long enrollmentId);
    List<EnrollmentResponseDTO> getAllEnrollment();
    void deleteEnrollment(Long enrollmentId);
    EnrollmentResponseDTO updateEnrollmentStatus (Long enrollmentId,String status);
    List<StudentCourseDTO> getCoursesByStudent(Long studentId,String query, Long academicPeriodId);
    List<CourseStudentDTO> getStudentsByCourse(Long courseId, String query);
}
