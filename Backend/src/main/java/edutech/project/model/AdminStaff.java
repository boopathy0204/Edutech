package edutech.project.model;

import jakarta.persistence.*;
import lombok.*;
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdminStaff {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long adminId;
    private String employeeCode;
    private String department;
    private String designation;
    private String contactNumber;
    private String firstName;
    private String lastName;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
}
