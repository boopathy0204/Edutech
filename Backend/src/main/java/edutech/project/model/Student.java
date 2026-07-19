package edutech.project.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long studentId;
    private String registrationNumber;
    private String firstName;
    private String lastName;
    private String phone;
    private String department;
    private String program;
    @ManyToOne
    @JoinColumn(name = "academic_period_id")
    private AcademicPeriod academicPeriod;

    public String getAcademicYear() {
        return academicPeriod != null ? academicPeriod.getAcademicYear() : "2026-2027";
    }

    public String getAcademicHalf() {
        return academicPeriod != null ? academicPeriod.getAcademicHalf() : "FIRST_HALF";
    }
    @CreationTimestamp
    private LocalDateTime created_at;
    @OneToOne(cascade=CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "student",cascade=CascadeType.ALL)
    private List<Enrollment> enrollments=new ArrayList<>();

    @OneToMany(mappedBy="student",cascade=CascadeType.ALL)
    private List<AcademicRecord> academicRecords=new ArrayList<>();

    @OneToMany(mappedBy="student",cascade=CascadeType.ALL)
    private List<Submission> submissions=new ArrayList<>();

    @OneToMany(mappedBy="student",cascade=CascadeType.ALL)
    private List<Notification> notifications=new ArrayList<>();


}
