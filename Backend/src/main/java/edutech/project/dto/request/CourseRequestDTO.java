package edutech.project.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CourseRequestDTO {
    @NotBlank(message = "Course code is required")
    private String courseCode;
    @NotBlank(message = "Course name is required")
    private String courseName;
    private String description;
    @Min(value = 1, message = "Credits must be at least 1")
    private Integer credits;
    @NotNull(message = "Professor Id is required")
    private Long professorId;

    private java.time.LocalDate enrollmentStartDate;
    private java.time.LocalDate enrollmentEndDate;
    private java.time.LocalDate courseStartDate;
    private java.time.LocalDate courseEndDate;
}
