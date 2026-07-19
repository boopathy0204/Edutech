package edutech.project.dashboard.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfessorProfileDTO {
    private Long professorId;
    private String employeeCode;
    private String fullName;
    private String department;
    private String designation;
}