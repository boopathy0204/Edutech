package edutech.project.dto.response;

import edutech.project.model.Submission;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class GradeResponseDTO {
    private Long gradeId;
    private Long submissionId;
    private Long studentId;
    private String studentName;
    private String registrationNumber;
    private Long assignmentId;
    private String assignmentTitle;
    private Double marks;
    private Integer maxmark;
    private Double percentage;
    private String letterGrade;
    private String feedback;
    private LocalDateTime gradedAt;
}
