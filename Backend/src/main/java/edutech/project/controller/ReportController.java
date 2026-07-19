package edutech.project.controller;

import edutech.project.dto.report.AcademicProgressDTO;
import edutech.project.dto.report.CourseParticipationDTO;
import edutech.project.dto.report.StudentPerformanceDTO;
import edutech.project.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/report")
public class ReportController {
    @Autowired
    private ReportService reportService;
    @PreAuthorize("hasAnyRole('PROFESSOR','ADMIN')")
    @GetMapping("/student-performance")
    public ResponseEntity<StudentPerformanceDTO> getStudentPerformanceReport(@RequestParam(value = "academicPeriodId", required = false) Long academicPeriodId) {
        return ResponseEntity.ok(reportService.getStudentPerformanceReport(academicPeriodId));
    }
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSOR')")
    @GetMapping("/course-participation")
    public ResponseEntity<List<CourseParticipationDTO>> getCourseParticipationReport(@RequestParam(value = "academicPeriodId", required = false) Long academicPeriodId) {
        return ResponseEntity.ok(reportService.getCourseParticipationReport(academicPeriodId));
    }
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/academic-progress")
    public ResponseEntity<List<AcademicProgressDTO>> getAcademicProgressReport(@RequestParam(value = "academicPeriodId", required = false) Long academicPeriodId) {
        return ResponseEntity.ok(reportService.getAcademicProgressReport(academicPeriodId));
    }
}
