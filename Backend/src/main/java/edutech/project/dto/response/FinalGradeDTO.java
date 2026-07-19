package edutech.project.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FinalGradeDTO {
    private Long studentId;
    private Long courseId;
    private String courseName;
    private Double averageMarks;
    private Double averagePercentage;
    private String finalLetterGrade;

}
