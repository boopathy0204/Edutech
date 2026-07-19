package edutech.project.service;

import edutech.project.dto.request.StudentRequestDTO;
import edutech.project.dto.response.BulkImportSummaryDTO;
import edutech.project.dto.response.StudentResponseDTO;
import org.springframework.web.multipart.MultipartFile;
import edutech.project.model.User;
import java.util.List;


public interface StudentService {
    StudentResponseDTO createStudent(StudentRequestDTO request);
    StudentResponseDTO getStudentById(Long studentId);
    List<StudentResponseDTO> getAllStudent();
    void deleteStudent(Long studentId);
    StudentResponseDTO updateStudent(Long studentId,StudentRequestDTO request);
    BulkImportSummaryDTO bulkImportStudents(MultipartFile file);
    StudentResponseDTO getStudentByUser(User user);
}
