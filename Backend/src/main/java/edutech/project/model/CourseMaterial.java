package edutech.project.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CourseMaterial {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long materialId;
    private String title;
    private String fileUrl;
    private String fileName;
    @CreationTimestamp
    private LocalDateTime uploadedAt;
    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;
}
