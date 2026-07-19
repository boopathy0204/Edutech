package edutech.project.dto.report;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CourseParticipationDTO {
    private Long courseId;
    private String courseName;
    private String courseCode;
    private Integer totalEnrollments;
    private String academicYear;
    private String academicHalf;
    private String courseStatus;
    private String professorName;
}
