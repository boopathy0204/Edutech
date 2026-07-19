package edutech.project.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProfessorRequestDTO {
    @NotBlank(message = "name is required")
    private String name;
    @NotBlank(message = "EmployeeCode is required")
    private String employeeCode;
    @NotBlank(message = "department is required")
    private String department;
    @NotBlank(message = "designation is required")
    private String designation;
    @NotNull(message = "User Id is required")
    @NotBlank(message = "ContactNumber is required")
    private String contactNumber;
    private Long userId;
}
