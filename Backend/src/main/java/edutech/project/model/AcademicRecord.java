package edutech.project.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AcademicRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long recordId;
    private String finalGrade;
    private Double gpa;
    private Integer semester;
    @ManyToOne
    @JoinColumn(name = "academic_period_id")
    private AcademicPeriod academicPeriod;

    public String getAcademicYear() {
        return academicPeriod != null ? academicPeriod.getAcademicYear() : "2026-2027";
    }

    public String getAcademicHalf() {
        return academicPeriod != null ? academicPeriod.getAcademicHalf() : "FIRST_HALF";
    }
    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;
    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;
}
