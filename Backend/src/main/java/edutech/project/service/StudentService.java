package edutech.project.service;

import edutech.project.dto.request.StudentRequestDTO;
import edutech.project.dto.response.BulkImportSummaryDTO;
import edutech.project.dto.response.StudentResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


public interface StudentService {
    StudentResponseDTO createStudent(StudentRequestDTO request);
    StudentResponseDTO getStudentById(Long studentId);
    List<StudentResponseDTO> getAllStudent(String query);
    void deleteStudent(Long studentId);
    StudentResponseDTO updateStudent(Long studentId,StudentRequestDTO request);
    StudentResponseDTO getStudentByUser(edutech.project.model.User user);
}
