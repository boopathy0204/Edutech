package edutech.project.dashboard.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentProfileDTO {
    private Long studentId;
    private String registrationNumber;
    private String fullName;
    private String phoneNumber;
    private String department;
    private String program;
    
    private String academicYear;
    private String academicHalf;
    private Double currentHalfGpa;
    private Double cgpa;
    private Integer totalCreditsEarned;
    private Integer totalCreditsCompleted;
}