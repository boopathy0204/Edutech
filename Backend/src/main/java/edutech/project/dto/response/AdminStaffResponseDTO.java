package edutech.project.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdminStaffResponseDTO {
    private Long adminId;
    private String employeeCode;
    private String department;
    private String designation;
    private String contactNumber;
    private String firstName;
    private String lastName;
    private Long userId;
    private String username;
}