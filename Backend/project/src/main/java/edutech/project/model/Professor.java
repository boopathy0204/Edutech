package edutech.project.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Professor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long professorId;
    private String name;
    private String department;
    private String designation;
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
}
