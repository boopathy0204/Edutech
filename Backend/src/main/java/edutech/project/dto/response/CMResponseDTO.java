package edutech.project.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CMResponseDTO {
    private Long materialId;
    private String title;
    private String fileUrl;
    private String filename ;
    private LocalDateTime uploadedAt;
    private Long courseId;
    private String courseName;
    private String courseCode;
}
