package edutech.project.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CourseResponseDTO {
    private Long courseId;
    private String courseCode;
    private String courseName;
    private String description;
    private Integer credits;
    private Long professorId;
    private String professorName;

    private String academicYear;
    private String academicHalf;
    private java.time.LocalDate enrollmentStartDate;
    private java.time.LocalDate enrollmentEndDate;
    private java.time.LocalDate courseStartDate;
    private java.time.LocalDate courseEndDate;
    private String courseStatus;
}
