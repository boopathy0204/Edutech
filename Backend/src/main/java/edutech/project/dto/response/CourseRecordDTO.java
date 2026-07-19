package edutech.project.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CourseRecordDTO {
    private Long courseId;
    private String courseName;
    private String professorName;
    private String enrollmentStatus;
    private Double averageMarks;
    private Double averagePercentage;
    private String finalGrade;
    private String academicYear;
    private String academicHalf;
}
