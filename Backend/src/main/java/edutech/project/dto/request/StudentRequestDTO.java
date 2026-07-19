package edutech.project.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StudentRequestDTO {
    @NotBlank(message = "registrationNumber is required")
    private String registrationNumber;
    @NotBlank(message = "firstName is required")
    private String firstName;
    private String lastName;
    @NotBlank(message = "phone is required")
    private String phone;
    @NotBlank(message = "department is required")
    private String department;
    @NotBlank(message = "program is required")
    private String program;
    @NotNull(message = "User Id is required")
    private Long userId;
}
