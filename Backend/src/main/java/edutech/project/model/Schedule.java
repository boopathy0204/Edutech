package edutech.project.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalTime;
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long scheduleId;
    private LocalTime startTime;
    private LocalTime endTime;
    private String roomNumber;
    @Enumerated(EnumType.STRING)
    private DayOfWeek day;
    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;
}
