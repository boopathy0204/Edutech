package edutech.project.service;

import edutech.project.dto.response.NotificationResponseDTO;
import edutech.project.model.Student;
import java.util.List;

public interface NotificationService {
    void createNotification(Student student, String title, String message);
    List<NotificationResponseDTO> getNotifications(Long studentId, Long academicPeriodId);
    void markAsRead(Long notificationId);
    List<NotificationResponseDTO> getUnreadNotifications(Long studentId);
}
