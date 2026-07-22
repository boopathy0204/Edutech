package edutech.project.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class AssignmentByCourseResponseDTO {
    private Long assignmentId;
    private String title;
    private String description;
    private LocalDate dueDate;
    private Integer maxMarks;
    private Long courseId;
    private String courseCode;
}
