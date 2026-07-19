package edutech.project.controller;

import edutech.project.dto.request.ScheduleRequestDTO;
import edutech.project.dto.response.ScheduleResponseDTO;
import edutech.project.service.ScheduleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/schedule")
public class ScheduleController {
    @Autowired
    private ScheduleService scheduleService;

    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSOR')")
    @PostMapping
    public ResponseEntity<ScheduleResponseDTO> createSchedule(@Valid @RequestBody ScheduleRequestDTO request) {
        ScheduleResponseDTO response = scheduleService.createSchedule(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSOR', 'STUDENT')")
    @GetMapping("/{scheduleId}/details")
    public ResponseEntity<ScheduleResponseDTO> getScheduleById(@PathVariable Long scheduleId) {
        return ResponseEntity.ok(scheduleService.getScheduleById(scheduleId));
    }
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSOR', 'STUDENT')")
    @GetMapping("/{courseId}/schedule-listbycourse")
    public ResponseEntity<List<ScheduleResponseDTO>> getScheduleByCourse(@PathVariable Long courseId) {
        return ResponseEntity.ok(scheduleService.getScheduleByCourse(courseId));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSOR')")
    @DeleteMapping("/{scheduleId}/delete")
    public ResponseEntity<String> deleteSchedule(@PathVariable Long scheduleId) {
        scheduleService.deleteSchedule(scheduleId);
        return ResponseEntity.ok("Schedule deleted successfully");
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT')")
    @GetMapping("/student/{studentId}/weekly-schedule")
    public ResponseEntity<List<ScheduleResponseDTO>> getScheduleByStudent(
            @PathVariable Long studentId,
            @RequestParam(required = false) Long academicPeriodId) {
        return ResponseEntity.ok(scheduleService.getScheduleByStudent(studentId, academicPeriodId));
    }
}
