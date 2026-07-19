package edutech.project.Repository;

import edutech.project.model.AdminStaff;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminStaffRepo extends JpaRepository<AdminStaff,Long> {
}
