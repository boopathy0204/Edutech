package edutech.project.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
@Data
@Builder
public class SubmissionResponseDTO {
   private Long submissionId;
    private Long studentId;
    private String studentName;
    private String registrationNumber;
    private Long assignmentId;
    private String assignmentTitle;
    private String fileName;
    private String fileUrl;
    private LocalDateTime submittedAt;
    private String status;
}
