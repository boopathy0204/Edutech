package edutech.project.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EnrollmentRequestDTO {
    @NotNull(message = "StudentId is required")
    private Long studentId;
    @NotNull(message ="CourseId is required")
    private Long courseId;
}
