package edutech.project.controller;

import edutech.project.model.AcademicPeriod;
import edutech.project.service.AcademicPeriodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/academic-periods")
public class AcademicPeriodController {
    @Autowired
    private AcademicPeriodService academicPeriodService;

    @GetMapping
    public ResponseEntity<List<AcademicPeriod>> getAllPeriods() {
        return ResponseEntity.ok(academicPeriodService.getAllPeriods());
    }
    @GetMapping("/active")
    public ResponseEntity<AcademicPeriod> getActivePeriod() {
        return ResponseEntity.ok(academicPeriodService.getActivePeriod());
    }
    @GetMapping("/{id}")
    public ResponseEntity<AcademicPeriod> getPeriodById(@PathVariable Long id) {
        return ResponseEntity.ok(academicPeriodService.getPeriodById(id));
    }
}
