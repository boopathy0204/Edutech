package edutech.project.serviceImpl;

import edutech.project.dto.response.NotificationResponseDTO;
import edutech.project.exception.ResourceNotFoundException;
import edutech.project.model.Notification;
import edutech.project.model.Student;
import edutech.project.repository.NotificationRepo;
import edutech.project.repository.StudentRepo;
import edutech.project.service.NotificationService;
import edutech.project.model.AcademicPeriod;
import edutech.project.repository.AcademicPeriodRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {
    @Autowired
    private StudentRepo studentRepo;
    @Autowired
    private NotificationRepo notificationRepo;
    @Autowired
    private AcademicPeriodRepo academicPeriodRepo;

    @Override
    public void createNotification(Student student,String title,String message) {
        Notification notification=new Notification();
        notification.setStudent(student);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setIsRead(false);
        notificationRepo.save(notification);
    }
    @Override
    public List<NotificationResponseDTO> getNotifications(Long studentId, Long academicPeriodId) {
        Student student=studentRepo.findById(studentId).orElseThrow(()->new ResourceNotFoundException("Student not found"));
        List<Notification> notifications=notificationRepo.findByStudent(student);
        
        if (academicPeriodId != null) {
            AcademicPeriod period = academicPeriodRepo.findById(academicPeriodId).orElse(null);
            if (period != null) {
                java.time.LocalDateTime start = period.getStartDate().atStartOfDay();
                java.time.LocalDateTime end = period.getEndDate().plusDays(1).atStartOfDay();
                
                List<NotificationResponseDTO> response=new ArrayList<>();
                for (Notification notification : notifications) {
                    java.time.LocalDateTime created = notification.getCreatedAt();
                    if (created != null && !created.isBefore(start) && created.isBefore(end)) {
                        response.add(mapToResponse(notification));
                    }
                }
                return response;
            }
        }

        List<NotificationResponseDTO> response=new ArrayList<>();
        for (Notification notification:notifications) {
            response.add(mapToResponse(notification));
        }
        return response;
    }

    @Override
    public void markAsRead(Long notificationId) {
        Notification notification=notificationRepo.findById(notificationId).orElseThrow(()->new ResourceNotFoundException("Notification not found"));
        notification.setIsRead(true);
        notificationRepo.save(notification);
    }
    @Override
    public List<NotificationResponseDTO> getUnreadNotifications(Long studentId) {
        Student student = studentRepo.findById(studentId).orElseThrow(() ->new ResourceNotFoundException("Student not found"));
        List<Notification> notifications = notificationRepo.findByStudentAndIsReadFalse(student);
        List<NotificationResponseDTO> response = new ArrayList<>();
        for (Notification notification : notifications) {
            response.add(mapToResponse(notification));
        }
        return response;
    }
    private NotificationResponseDTO mapToResponse(Notification notification) {
        return NotificationResponseDTO.builder()
                .notificationId(notification.getNotificationId())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .isRead(notification.getIsRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}

