package edutech.project.service;

import edutech.project.dto.request.AssignmentRequestDTO;
import edutech.project.dto.response.AssignmentByCourseResponseDTO;
import edutech.project.dto.response.AssignmentResponseDTO;
import edutech.project.model.Grade;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AssignmentService {
    AssignmentResponseDTO createAssignment(AssignmentRequestDTO request);
    AssignmentResponseDTO getAssignmentById(Long assignmentId);
    List<AssignmentResponseDTO> getAllAssignment();
    List<AssignmentByCourseResponseDTO> getAssignmentByCourse(Long courseId);
    void deleteAssignment(Long assignmentId);
    AssignmentResponseDTO updateAssignment(Long assignmentId,AssignmentRequestDTO request);
    List<AssignmentResponseDTO> getAssignmentByProfessor(Long professorId, Long courseId, String query, Long academicPeriodId);

}
