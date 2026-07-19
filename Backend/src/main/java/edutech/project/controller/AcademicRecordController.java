package edutech.project.controller;

import edutech.project.dto.response.AcademicRecordResponseDTO;
import edutech.project.service.AcademicRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/academic-records")
public class AcademicRecordController {
    @Autowired
    private AcademicRecordService academicRecordService;
    @PreAuthorize("hasAnyRole('ADMIN','STUDENT')")
    @GetMapping("/{studentId}/student-list")
    public ResponseEntity<AcademicRecordResponseDTO> getAcademicRecord(
            @PathVariable Long studentId,
            @RequestParam(value = "academicPeriodId", required = false) Long academicPeriodId) {
        return ResponseEntity.ok(academicRecordService.getAcademicRecord(studentId, academicPeriodId));
    }
}
