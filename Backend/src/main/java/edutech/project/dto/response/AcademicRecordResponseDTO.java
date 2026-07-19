package edutech.project.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AcademicRecordResponseDTO {
    private Long studentId;
    private String studentName;
    private Integer totalCourses;
    private Double cgpa;
    private List<CourseRecordDTO> courses;
}

