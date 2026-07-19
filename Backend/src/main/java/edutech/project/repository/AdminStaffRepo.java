package edutech.project.repository;

import edutech.project.model.AdminStaff;
import edutech.project.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminStaffRepo extends JpaRepository<AdminStaff, Long> {
    boolean existsByEmployeeCode(String employeeCode);
    boolean existsByEmployeeCodeAndAdminIdNot(String employeeCode, Long adminId);
    boolean existsByUser(User user);
    boolean existsByUserAndAdminIdNot(User user, Long adminId);
}
