package edutech.project.dto.response;

import edutech.project.model.Course;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AssignmentResponseDTO {
    private Long assignmentId;
    private String title;
    private String description;
    private LocalDateTime dueDate;
    private Integer maxMarks;
    private Long courseId;
    private String courseName;
    private String courseCode;
}
