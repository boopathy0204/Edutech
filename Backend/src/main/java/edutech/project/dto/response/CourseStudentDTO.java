package edutech.project.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
@Data
@Builder
public class CourseStudentDTO {
    private Long studentId;
    private String studentName;
    private String status;
    private LocalDate enrollmentDate;
    private String registrationNumber;
}
