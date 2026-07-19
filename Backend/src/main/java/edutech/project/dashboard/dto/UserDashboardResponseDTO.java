package edutech.project.dashboard.dto;

import edutech.project.dashboard.dto.AccountDTO;
import edutech.project.dashboard.dto.StatisticsDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDashboardResponseDTO {
    private AccountDTO account;
    private StudentProfileDTO studentProfile;
   private ProfessorProfileDTO professorProfile;
    private StatisticsDTO statistics;
    private List<CourseDashboardDTO> courses;
    private List<AssignmentDashboardDTO> assignments;
}
