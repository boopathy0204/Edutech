package edutech.project.controller;

import edutech.project.dto.response.NotificationResponseDTO;
import edutech.project.model.Notification;
import edutech.project.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notification")
public class NotificationController {
    @Autowired
    private NotificationService notificationService;
    @GetMapping("/{studentId}/notification-listbystudent")
    public ResponseEntity<List<NotificationResponseDTO>> getNotifications(
            @PathVariable Long studentId,
            @RequestParam(required = false) Long academicPeriodId) {
        return ResponseEntity.ok(notificationService.getNotifications(studentId, academicPeriodId));
    }
    @PutMapping("/{notificationId}/read")
    public ResponseEntity<String> markAsRead(@PathVariable Long notificationId) {
        notificationService.markAsRead(notificationId);
        return ResponseEntity.ok("Notification marked as read");
    }
    @PutMapping("/read/{notificationId}")
    public ResponseEntity<String> markAsReadAlt(@PathVariable Long notificationId) {
        notificationService.markAsRead(notificationId);
        return ResponseEntity.ok("Notification marked as read");
    }
    @GetMapping("/{studentId}/unread")
    public ResponseEntity<List<NotificationResponseDTO>> getUnreadNotifications(@PathVariable Long studentId) {
        return ResponseEntity.ok(notificationService.getUnreadNotifications(studentId));
    }
}