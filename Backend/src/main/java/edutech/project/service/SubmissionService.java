package edutech.project.service;

import edutech.project.dto.request.SubmissionRequestDTO;
import edutech.project.dto.response.SubmissionCenterResponseDTO;
import edutech.project.dto.response.SubmissionResponseDTO;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface SubmissionService {
    SubmissionResponseDTO createSubmission(SubmissionRequestDTO request, MultipartFile file);
    SubmissionResponseDTO getSubmissionById(Long submissionId);
    List<SubmissionResponseDTO> getAllSubmissions();
    List<SubmissionResponseDTO> getSubmissionsByStudent(Long studentId);
    List<SubmissionResponseDTO> getSubmissionsByAssignment(Long assignmentId, String status, String query);
    void deleteSubmission(Long submissionId);
    Resource downloadSubmission(Long submissionId);
    List<SubmissionCenterResponseDTO> getSubmissionCenterByProfessor(Long professorId, Long courseId, String status, String query, Long academicPeriodId);

    }
