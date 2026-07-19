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
public class Assignment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long assignmentId;
    private String title;
    @Column(length = 2000)
    private String description;
    private LocalDateTime dueDate;
    private Integer maxMarks;
    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;
}