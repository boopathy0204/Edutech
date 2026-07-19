package edutech.project.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfessorSummary {
    private StatisticsDTO statistics;
    private List<CourseDashboardDTO> courses;
    private List<AssignmentDashboardDTO> assignments;
}