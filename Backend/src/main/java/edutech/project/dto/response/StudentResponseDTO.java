package edutech.project.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
@Data
@Builder
public class StudentResponseDTO {
    private Long studentId;
    private String registrationNumber;
    private String firstName;
    private String lastName;
    private String phone;
    private String department;
    private String program;
    private LocalDateTime created_at;
    private Long userId;
    private String username;
    private String academicYear;
    private String academicHalf;
}
