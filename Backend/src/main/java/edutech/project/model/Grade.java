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
public class Grade {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long gradeId;
    private Double marks;
    private Double percentage;
    private String letterGrade;
    @Column(length = 1000)
    private String feedback;
    @CreationTimestamp
    private LocalDateTime gradedAt;
    @OneToOne
    @JoinColumn(name = "submission_id")
    private Submission submission;
}
