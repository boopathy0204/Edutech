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

public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long studentId;
    private String firstName;
    private String lastName;
    private String phone;
    private String department;
    private String program;
    @CreationTimestamp
    private LocalDateTime created_at;
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
}
