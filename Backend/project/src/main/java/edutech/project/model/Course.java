package edutech.project.model;

import jakarta.persistence.*;
import lombok.*;
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long courseId;
    private String courseCode;
    private String courseName;
    @Column(length = 2000)
    private String description;
    private Integer credits;
    @ManyToOne
    @JoinColumn(name = "professor_id")
    private Professor professor;
}
