package edutech.project.dashboard.dto;
import edutech.project.model.EnrollmentStatus;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseDashboardDTO {
    private Long courseId;
    private String courseCode;
    private String courseName;
    private String professorName;
    private EnrollmentStatus enrollmentStatus;
    private Integer totalAssignments;
    private Integer submittedAssignments;
    private Integer pendingAssignments;
}