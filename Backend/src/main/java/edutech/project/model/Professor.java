package edutech.project.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Professor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long professorId;
    private String employeeCode;
    private String name;
    private String contactNumber;
    private String department;
    private String designation;
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
    @OneToMany(mappedBy = "professor",cascade =CascadeType.ALL)
    private List<Course> courses=new ArrayList<>();
}
