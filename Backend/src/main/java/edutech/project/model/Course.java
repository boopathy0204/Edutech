package edutech.project.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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

    @ManyToOne
    @JoinColumn(name = "academic_period_id")
    private AcademicPeriod academicPeriod;

    public String getAcademicYear() {
        return academicPeriod != null ? academicPeriod.getAcademicYear() : "2026-2027";
    }

    public String getAcademicHalf() {
        return academicPeriod != null ? academicPeriod.getAcademicHalf() : "FIRST_HALF";
    }
    private LocalDate enrollmentStartDate;
    private LocalDate enrollmentEndDate;
    private LocalDate courseStartDate;
    private LocalDate courseEndDate;
    private String courseStatus = "UPCOMING";

    public String getCourseStatus() {
        if ("COMPLETED".equalsIgnoreCase(this.courseStatus)) {
            return "COMPLETED";
        }
        LocalDate now = LocalDate.now();
        if (this.courseEndDate != null && now.isAfter(this.courseEndDate)) {
            return "COMPLETED";
        }
        if (this.courseStartDate != null && now.isBefore(this.courseStartDate)) {
            return "UPCOMING";
        }
        return "ACTIVE";
    }

    @OneToMany(mappedBy = "course",cascade=CascadeType.ALL)
    private List<AcademicRecord> academicRecords=new ArrayList<>();

    @OneToMany(mappedBy = "course",cascade = CascadeType.ALL)
    private List<Enrollment> enrollments=new ArrayList<>();

    @OneToMany(mappedBy = "course",cascade = CascadeType.ALL)
    private List<Assignment> assignments=new ArrayList<>();

    @OneToMany(mappedBy = "course",cascade = CascadeType.ALL)
    private List<CourseMaterial> courseMaterials=new ArrayList<>();

    @OneToMany(mappedBy = "course",cascade = CascadeType.ALL)
    private List<Schedule> schedules=new ArrayList<>();

}
