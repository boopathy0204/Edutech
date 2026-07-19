package edutech.project.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
@Data
@Builder
public class StudentCourseDTO {
    private Long courseId;
    private String courseName;
    private LocalDate enrollmentDate;
    private String status;
    private String courseCode;
    private String description;
    private Integer credits;
    private String professorName;
}
