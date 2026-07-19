package edutech.project.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GradeCenterResponseDTO {
    private Long gradeId;
    private Long courseId;
    private String courseCode;
    private String courseName;
    private Long assignmentId;
    private String assignmentTitle;
    private Integer maxMarks;
    private Long studentId;
    private String studentName;
    private String registrationNumber;
    private Double marks;
    private Double percentage;
    private String letterGrade;
    private String feedback;
    private LocalDateTime gradedAt;
}
