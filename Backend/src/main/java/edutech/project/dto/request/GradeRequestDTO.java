package edutech.project.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GradeRequestDTO {
    @NotNull(message = "submissionId is required")
    private Long submissionId;
    @NotNull(message = "mark ir required")
    private Double marks;
    private String feedback;
}
