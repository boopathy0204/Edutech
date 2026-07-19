package edutech.project.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AdminStaffRequestDTO {
    @NotBlank(message = "EmployeeCode is required")
    private String employeeCode;
    @NotBlank(message = "department is required")
    private String department;
    @NotBlank(message = "designation is required")
    private String designation;
    @NotBlank(message = "FirstName is required")
    private String firstName;
    @NotBlank(message="LastName is required")
    private String lastName;
    @NotBlank(message = "ContactNumber is required")
    private String contactNumber;

    @NotNull(message = "User Id is required")
    private Long userId;
}
