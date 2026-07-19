package edutech.project.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfessorAssignmentDashboardDTO {
    private Long assignmentId;
    private String assignmentTitle;
    private String courseName;
    private LocalDateTime dueDate;
    private Integer totalSubmissions;
    private Integer gradedSubmissions;
    private Integer pendingGrading;

}