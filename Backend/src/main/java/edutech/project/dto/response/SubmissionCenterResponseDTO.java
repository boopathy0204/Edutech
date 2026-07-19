package edutech.project.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubmissionCenterResponseDTO {
    private Long submissionId;
    private Long courseId;
    private String courseCode;
    private Long assignmentId;
    private String assignmentTitle;
    private Long studentId;
    private String studentName;
    private String registrationNumber;
    private String fileName;
    private String fileUrl;
    private LocalDateTime submittedAt;
    private String status;
    private Double marks;
    private Integer maxMarks;
}