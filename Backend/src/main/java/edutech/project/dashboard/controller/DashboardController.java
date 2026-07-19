package edutech.project.dashboard.controller;

import edutech.project.dashboard.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")

public class DashboardController {
    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/{userId}/user-details")
    public ResponseEntity<?> getDashboard(
            @PathVariable Long userId,
            @RequestParam(required = false) Long academicPeriodId) {
        return ResponseEntity.ok(dashboardService.getDashboard(userId, academicPeriodId));

    }

}