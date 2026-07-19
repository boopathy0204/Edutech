package edutech.project.controller;

import edutech.project.dto.request.EnrollmentRequestDTO;
import edutech.project.dto.response.CourseStudentDTO;
import edutech.project.dto.response.EnrollmentResponseDTO;
import edutech.project.dto.response.StudentCourseDTO;
import edutech.project.model.Enrollment;
import edutech.project.service.EnrollmentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@PreAuthorize("hasAnyRole('ADMIN','STUDENT','PROFESSOR')")
@RestController
@RequestMapping("/api/enrollment")
public class EnrollmentController {
    @Autowired
    private EnrollmentService enrollmentService;
     @PostMapping
    public ResponseEntity<EnrollmentResponseDTO> createEnrollment(@Valid @RequestBody EnrollmentRequestDTO request){
         EnrollmentResponseDTO response=enrollmentService.createEnrollment(request);
         return new ResponseEntity<>(response,HttpStatus.CREATED);
     }

     @GetMapping
    public ResponseEntity<List<EnrollmentResponseDTO>> getAllEnrollments(){
         return ResponseEntity.ok(enrollmentService.getAllEnrollment());
     }
      @GetMapping("/{enrollmentId}/enrollment-details")
     public ResponseEntity<EnrollmentResponseDTO> getEnrollmentById(@PathVariable Long enrollmentId){
          return ResponseEntity.ok(enrollmentService.getEnrollmentById(enrollmentId));
      }
      @PatchMapping("/{enrollmentId}/update-status")
     public ResponseEntity<EnrollmentResponseDTO> updateEnrollmentStatus(@PathVariable Long enrollmentId,@RequestBody String status){
          return ResponseEntity.ok(enrollmentService.updateEnrollmentStatus(enrollmentId,status));
      }
      @DeleteMapping("/{enrollmentId}/delete")
     public ResponseEntity<String> deleteEnrollment(@PathVariable Long enrollmentId){
          enrollmentService.deleteEnrollment(enrollmentId);
          return ResponseEntity.ok("Deleted successfully");
      }
     @GetMapping("/{studentId}/course-listbystudent")
     public ResponseEntity<List<StudentCourseDTO>> getCoursesByStudent(@PathVariable Long studentId,
             @RequestParam(value = "query", required = false) String query,
             @RequestParam(value = "academicPeriodId", required = false) Long academicPeriodId) {
         return ResponseEntity.ok(enrollmentService.getCoursesByStudent(studentId, query, academicPeriodId));
     }
     @GetMapping("/{courseId}/student-listbycourse")
     public ResponseEntity<List<CourseStudentDTO>> getStudentsByCourse(
             @PathVariable Long courseId,
             @RequestParam(required = false) String query) {
         return ResponseEntity.ok(enrollmentService.getStudentsByCourse(courseId, query));
     }

}
