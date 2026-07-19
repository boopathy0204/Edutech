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
public class Grade {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long gradeId;
    private Double marks;
    @Column(length = 1000)
    private String feedback;
    private LocalDateTime gradedAt;
    @OneToOne
    @JoinColumn(name = "submission_id")
    private Submission submission;
}
