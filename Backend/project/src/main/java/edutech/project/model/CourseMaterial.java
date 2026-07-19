package edutech.project.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseMaterial {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long materialId;
    private String title;
    private String fileUrl;
    private LocalDateTime uploadedAt;
    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;
}
