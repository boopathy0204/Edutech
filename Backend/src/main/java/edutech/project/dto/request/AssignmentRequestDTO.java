package edutech.project.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


import java.time.LocalDateTime;

@Data
public class AssignmentRequestDTO {
    @NotBlank(message = "title is required")
    private String title;
    @NotBlank(message = "description is required")
    private String description;
    @NotNull(message = "dueDate is required")
    private LocalDateTime dueDate;
    @Min(value=0)
    private Integer maxMarks;
    @NotNull(message = "courseId is required")
    private Long courseId;
}
