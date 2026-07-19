package edutech.project.controller;

import edutech.project.dto.response.FinalGradeDTO;
import edutech.project.service.GradeCalculationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/final-grade")
public class GradeCalculationController {
    @Autowired
    private GradeCalculationService gradeCalculationService;

    @GetMapping("/{studentId}/{courseId}/calculate")
    public ResponseEntity<FinalGradeDTO> calculateFinalGrade(@PathVariable Long studentId, @PathVariable Long courseId) {
        return ResponseEntity.ok(gradeCalculationService.calculateFinalGrade(studentId, courseId));
    }
}
