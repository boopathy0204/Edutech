package edutech.project.repository;

import edutech.project.model.Notification;
import edutech.project.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepo extends JpaRepository<Notification, Long> {
    List<Notification> findByStudent(Student student);
    List<Notification> findByStudentAndIsReadFalse(Student student);
}
