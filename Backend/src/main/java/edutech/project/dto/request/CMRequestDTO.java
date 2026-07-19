package edutech.project.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CMRequestDTO {
    @NotBlank(message = "title is required")
    private String title;
    @NotNull(message = "courseId is required")
    private Long courseId;
}
