package edutech.project.Repository;

import edutech.project.model.CourseMaterial;
import org.springframework.data.jpa.repository.JpaRepository;

import java.beans.JavaBean;

public interface CourseMaterialRepo extends JpaRepository<CourseMaterial,Long> {
}
