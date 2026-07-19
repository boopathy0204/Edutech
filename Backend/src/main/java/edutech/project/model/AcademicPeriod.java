package edutech.project.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "academic_period")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AcademicPeriod {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long academicPeriodId;
    private String academicYear;
    private String academicHalf;
    private String status;
    private LocalDate startDate;
    private LocalDate endDate;
}
