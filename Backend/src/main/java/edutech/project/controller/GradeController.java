package edutech.project.controller;

import edutech.project.dto.request.GradeRequestDTO;
import edutech.project.dto.response.GradeCenterResponseDTO;
import edutech.project.dto.response.GradeResponseDTO;
import edutech.project.repository.GradeRepo;
import edutech.project.service.GradeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/grade")
public class GradeController {
    @Autowired
    private GradeService gradeService;

    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSOR')")
    @PostMapping
    public ResponseEntity<GradeResponseDTO> createGrade(@Valid @RequestBody GradeRequestDTO request) {
        GradeResponseDTO response = gradeService.createGrade(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{gradeId}/grade-details")
    public ResponseEntity<GradeResponseDTO> getGradeById(@PathVariable Long gradeId) {
        return ResponseEntity.ok(gradeService.getGradeById(gradeId));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSOR')")
    @GetMapping
    public ResponseEntity<List<GradeResponseDTO>> getAllGrades() {
        return ResponseEntity.ok(gradeService.getAllGrades());
    }

    @GetMapping("/{studentId}/student-list")
    public ResponseEntity<List<GradeResponseDTO>> getGradesByStudent(
            @PathVariable Long studentId,
            @RequestParam(required = false) Long academicPeriodId) {
        return ResponseEntity.ok(gradeService.getGradeByStudent(studentId, academicPeriodId));
    }

    @GetMapping("/{assignmentId}/assignment-list")
    public ResponseEntity<List<GradeResponseDTO>> getGradesByAssignment(@PathVariable Long assignmentId) {
        return ResponseEntity.ok(gradeService.getGradeByAssignment(assignmentId));
    }

    @DeleteMapping("/{gradeId}/delete-grade")
    public ResponseEntity<String> deleteGrade(@PathVariable Long gradeId) {
        gradeService.deleteGrade(gradeId);
        return ResponseEntity.ok("Grade deleted successfully");
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSOR')")
    @GetMapping("/grade-center/professor/{professorId}")
    public ResponseEntity<List<GradeCenterResponseDTO>> getGradeCenter(
            @PathVariable Long professorId, 
            @RequestParam(required = false) Long courseId, 
            @RequestParam(required = false) String query,
            @RequestParam(required = false) Long academicPeriodId) {
        return ResponseEntity.ok(gradeService.getGradeCenterByProfessor(professorId, courseId, query, academicPeriodId));
    }
}
