package edutech.project.dto.report;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AcademicProgressDTO {
    private Long studentId;
    private String studentName;
    private Double cgpa;
    private Integer totalCourses;
}
