package edutech.project.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

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
    @NotNull(message = "Enrollment start date is required")
    private LocalDate enrollmentStartDate;
    @NotNull(message = "Enrollment end date is required")
    private LocalDate enrollmentEndDate;
    @NotNull(message = "Course start date is required")
    private LocalDate courseStartDate;
    @NotNull(message = "Course end date is required")
    private LocalDate courseEndDate;
}
