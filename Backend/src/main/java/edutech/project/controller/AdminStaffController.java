package edutech.project.controller;

import edutech.project.dto.request.AdminStaffRequestDTO;
import edutech.project.dto.response.AdminStaffResponseDTO;
import edutech.project.service.AdminStaffService;
import edutech.project.service.AcademicPeriodService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin-staff")
public class AdminStaffController {

    @Autowired
    private AdminStaffService adminStaffService;

    @Autowired
    private AcademicPeriodService academicPeriodService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<AdminStaffResponseDTO> createAdminStaff(@Valid @RequestBody AdminStaffRequestDTO request) {
        AdminStaffResponseDTO response = adminStaffService.createAdminStaff(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<AdminStaffResponseDTO>> getAllAdminStaff(
            @RequestParam(required = false) String query) {
        return ResponseEntity.ok(adminStaffService.getAllAdminStaff(query));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{adminId}/admin-details")
    public ResponseEntity<AdminStaffResponseDTO> getAdminStaffById(@PathVariable Long adminId) {
        return ResponseEntity.ok(adminStaffService.getAdminStaffById(adminId));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{adminId}/update")
    public ResponseEntity<AdminStaffResponseDTO> updateAdminStaff(@PathVariable Long adminId,
                                                                  @Valid @RequestBody AdminStaffRequestDTO request) {
        return ResponseEntity.ok(adminStaffService.updateAdminStaff(adminId, request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{adminId}/delete-admin")
    public ResponseEntity<String> deleteAdminStaff(@PathVariable Long adminId) {
        adminStaffService.deleteAdminStaff(adminId);
        return ResponseEntity.ok("Deleted successfully");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/advance-period")
    public ResponseEntity<String> advanceAcademicPeriod() {
        academicPeriodService.advancePeriod();
        return ResponseEntity.ok("Academic period advanced successfully.");
    }

    @PreAuthorize("hasAnyRole('ADMIN','PROFESSOR','STUDENT')")
    @GetMapping("/current-period")
    public ResponseEntity<?> getCurrentPeriod() {
        edutech.project.model.AcademicPeriod activePeriod = academicPeriodService.getActivePeriod();
        java.util.Map<String, Object> response = new java.util.HashMap<>();
        response.put("currentAcademicYear", activePeriod.getAcademicYear());
        response.put("currentAcademicHalf", activePeriod.getAcademicHalf());
        return ResponseEntity.ok(response);
    }
}