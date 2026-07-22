package edutech.project.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class AssignmentResponseDTO {
    private Long assignmentId;
    private String title;
    private String description;
    private LocalDate dueDate;
    private Integer maxMarks;
    private Long courseId;
    private String courseName;
    private String courseCode;
}
