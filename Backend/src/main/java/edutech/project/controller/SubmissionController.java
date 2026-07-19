package edutech.project.controller;

import edutech.project.dto.request.SubmissionRequestDTO;
import edutech.project.dto.response.SubmissionCenterResponseDTO;
import edutech.project.dto.response.SubmissionResponseDTO;
import edutech.project.repository.SubmissionRepo;
import edutech.project.service.SubmissionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/submission")
public class SubmissionController {
    @Autowired
    private SubmissionService submissionService;
    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT')")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SubmissionResponseDTO> createSubmission(@RequestPart("submission") @Valid SubmissionRequestDTO request, @RequestPart("file") MultipartFile file) {
        SubmissionResponseDTO response = submissionService.createSubmission(request, file);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSOR')")
    @GetMapping
    public ResponseEntity<List<SubmissionResponseDTO>> getAllSubmission(){
        return ResponseEntity.ok(submissionService.getAllSubmissions());
    }
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSOR', 'STUDENT')")
    @GetMapping("/{submissionId}/submission-details")
    public ResponseEntity<SubmissionResponseDTO> getSubmissionById(@PathVariable Long submissionId) {
        return ResponseEntity.ok(submissionService.getSubmissionById(submissionId));
    }
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSOR', 'STUDENT')")
    @GetMapping("/{studentId}/submission-listbystudent")
    public ResponseEntity<List<SubmissionResponseDTO>> getSubmissionsByStudent(@PathVariable Long studentId) {
        return ResponseEntity.ok(submissionService.getSubmissionsByStudent(studentId));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSOR')")
    @GetMapping("/{assignmentId}/submission-listbyassignment")
    public ResponseEntity<List<SubmissionResponseDTO>> getSubmissionsByAssignment(
            @PathVariable Long assignmentId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String query) {
        return ResponseEntity.ok(submissionService.getSubmissionsByAssignment(assignmentId, status, query));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{submissionId}/delete-submission")
    public ResponseEntity<String> deleteSubmission(@PathVariable Long submissionId) {
        submissionService.deleteSubmission(submissionId);
        return ResponseEntity.ok("Submission deleted successfully");
    }
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSOR')")
    @GetMapping("/professor/{professorId}/submission-center")
    public ResponseEntity<List<SubmissionCenterResponseDTO>> getSubmissionCenter(@PathVariable Long professorId,
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String query,
            @RequestParam(required = false) Long academicPeriodId) {
        return ResponseEntity.ok(submissionService.getSubmissionCenterByProfessor(professorId, courseId, status, query, academicPeriodId));
    }
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSOR', 'STUDENT')")
    @GetMapping("/{submissionId}/download-submission")
    public ResponseEntity<Resource> downloadSubmission(@PathVariable Long submissionId) {
        Resource resource = submissionService.downloadSubmission(submissionId);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}
