package edutech.project.controller;

import edutech.project.dto.request.AssignmentRequestDTO;
import edutech.project.dto.response.AssignmentByCourseResponseDTO;
import edutech.project.dto.response.AssignmentResponseDTO;
import edutech.project.dto.response.SubmissionCenterResponseDTO;
import edutech.project.service.AssignmentService;
import jakarta.validation.Valid;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assignment")
public class AssignmentController {
    @Autowired
    private AssignmentService assignmentService;
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSOR')")
    @PostMapping
    public ResponseEntity<AssignmentResponseDTO> createAssignment(@Valid @RequestBody AssignmentRequestDTO request){
        AssignmentResponseDTO response=assignmentService.createAssignment(request);
        return new ResponseEntity<>(response,HttpStatus.CREATED);
    }
    @GetMapping
    public ResponseEntity<List<AssignmentResponseDTO>> getAllAssignment(){
        return ResponseEntity.ok(assignmentService.getAllAssignment());
    }
    @GetMapping("/{assignmentId}/assignment-details")
    public ResponseEntity<AssignmentResponseDTO> getAssignmentById(@PathVariable Long assignmentId){
        return ResponseEntity.ok(assignmentService.getAssignmentById(assignmentId));
    }
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSOR')")
    @DeleteMapping("/{assignmentId}/delete-assignment")
    public ResponseEntity<String> deleteAssignment(@PathVariable Long assignmentId){
        assignmentService.deleteAssignment(assignmentId);
        return  ResponseEntity.ok("Deleted successfully");
    }
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSOR')")
    @PutMapping("/{assignmentId}/update-assignment")
    public ResponseEntity<AssignmentResponseDTO> updateAssignment(@PathVariable Long assignmentId,@Valid @RequestBody AssignmentRequestDTO request){
        AssignmentResponseDTO response=assignmentService.updateAssignment(assignmentId,request);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/{courseId}/course-list")
    public ResponseEntity<List<AssignmentByCourseResponseDTO>> getAssignmentByCourse(@PathVariable Long courseId){
        return ResponseEntity.ok(assignmentService.getAssignmentByCourse(courseId));
    }


    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSOR')")
    @GetMapping("/{professorId}/professor-assignment")
    public ResponseEntity<List<AssignmentResponseDTO>> getSubmissionCenter(@PathVariable Long professorId,
                                                                                  @RequestParam(required = false) Long courseId,
                                                                                  @RequestParam(required = false) String query,
                                                                                  @RequestParam(required = false) Long academicPeriodId) {
        return ResponseEntity.ok(assignmentService.getAssignmentByProfessor(professorId, courseId, query, academicPeriodId));
    }
}
