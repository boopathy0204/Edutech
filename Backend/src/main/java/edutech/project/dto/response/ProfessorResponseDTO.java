package edutech.project.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProfessorResponseDTO {
    private Long professorId;
    private String employeeCode;
    private String name;
    private String department;
    private String designation;
    private String contactNumber;
    private Long userId;
    private String username;

}
