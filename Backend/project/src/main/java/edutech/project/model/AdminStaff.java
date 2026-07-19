package edutech.project.model;

import jakarta.persistence.*;
import lombok.*;
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminStaff {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long adminId;
    private String employeeCode;
    private String department;
    private String designation;
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
}
