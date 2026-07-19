package edutech.project.dashboard.dto;

import edutech.project.dashboard.dto.AssignmentDashboardDTO;
import edutech.project.dashboard.dto.StatisticsDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class StudentAssignmentSummary {
    private StatisticsDTO statistics;
    private List<AssignmentDashboardDTO> assignments;
}
