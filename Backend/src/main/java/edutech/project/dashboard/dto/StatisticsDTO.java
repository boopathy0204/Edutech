package edutech.project.dashboard.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatisticsDTO {

    private Integer totalCourses;
    private Integer totalAssignments;
    private Integer completedAssignments;
    private Integer pendingAssignments;
    private Integer gradedAssignments;
    private Integer awaitingGrading;

}