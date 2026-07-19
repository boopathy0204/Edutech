package edutech.project.dto.report;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StudentPerformanceDTO {
    private Long totalStudents;
    private Long studentsWithGrades;
    private Double averagePercentage;
    private Double highestPercentage;
    private Double lowestPercentage;
}
