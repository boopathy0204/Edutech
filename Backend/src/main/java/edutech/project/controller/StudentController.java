package edutech.project.controller;

import edutech.project.dto.request.StudentRequestDTO;
import edutech.project.dto.response.StudentResponseDTO;
import edutech.project.model.User;
import edutech.project.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/student")
public class StudentController {
    @Autowired
    private StudentService studentService;

    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT')")
    @PostMapping
    public ResponseEntity<StudentResponseDTO> createStudent(@Valid @RequestBody StudentRequestDTO request) {
        StudentResponseDTO response = studentService.createStudent(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<StudentResponseDTO>> getAllStudent(
            @RequestParam(required = false) String query) {
        List<StudentResponseDTO> response = studentService.getAllStudent(query);
        return ResponseEntity.ok(response);
    }
    @PreAuthorize("hasAnyRole('ADMIN','PROFESSOR','STUDENT')")
    @GetMapping("/{studentId}/student-details")
    public ResponseEntity<StudentResponseDTO> getStudentById(@PathVariable Long studentId) {
        return ResponseEntity.ok(studentService.getStudentById(studentId));
    }
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{studentId}/delete-std")
    public ResponseEntity<String> deleteStudent(@PathVariable Long studentId) {
        studentService.deleteStudent(studentId);
        return ResponseEntity.ok("deleted successfully");
    }
    @PreAuthorize("hasAnyRole('ADMIN','STUDENT')")
    @PutMapping("/{studentId}/update-std")
    public ResponseEntity<StudentResponseDTO> updateStudent(@PathVariable Long studentId, @Valid @RequestBody StudentRequestDTO request) {
        return ResponseEntity.ok(studentService.updateStudent(studentId, request));
    }
    @PreAuthorize("hasRole('STUDENT')")
    @GetMapping("/student-profile")
    public ResponseEntity<StudentResponseDTO> getCurrentStudent(@AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(studentService.getStudentByUser(currentUser));
    }
}