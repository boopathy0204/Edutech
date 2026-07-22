package edutech.project.dashboard.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssignmentDashboardDTO {
    private Long assignmentId;
    private String assignmentTitle;
    private String courseName;
    private LocalDate dueDate;
    private String submissionStatus;
    private LocalDateTime submissionDate;
    private Double marksObtained;
    private String letterGrade;
    private String courseCode;
}