package edutech.project.service;

import edutech.project.dto.report.AcademicProgressDTO;
import edutech.project.dto.report.CourseParticipationDTO;
import edutech.project.dto.report.StudentPerformanceDTO;
import java.util.List;

public interface ReportService {
    StudentPerformanceDTO getStudentPerformanceReport(Long academicPeriodId);
    List<CourseParticipationDTO> getCourseParticipationReport(Long academicPeriodId);
    List<AcademicProgressDTO> getAcademicProgressReport(Long academicPeriodId);
}
