package edutech.project.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SubmissionRequestDTO {
    @NotNull(message = "studentId is required")
    private Long studentId;
    @NotNull(message = "assignment is required")
    private Long assignmentId;
}
