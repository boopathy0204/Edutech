package edutech.project.serviceImpl;

import edutech.project.dto.request.SubmissionRequestDTO;
import edutech.project.dto.response.SubmissionCenterResponseDTO;
import edutech.project.dto.response.SubmissionResponseDTO;
import edutech.project.exception.DuplicateResourceException;
import edutech.project.exception.ResourceNotFoundException;
import edutech.project.model.*;
import edutech.project.repository.AssignmentRepo;
import edutech.project.repository.EnrollmentRepo;
import edutech.project.repository.StudentRepo;
import edutech.project.repository.SubmissionRepo;
import edutech.project.service.SubmissionService;
import edutech.project.storage.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class SubmissionServiceImpl implements SubmissionService {
    @Autowired
    private SubmissionRepo submissionRepo;
    @Autowired
    private AssignmentRepo assignmentRepo;
    @Autowired
    private StudentRepo studentRepo;
    @Autowired
    private EnrollmentRepo enrollmentRepo;
    @Autowired
    private FileStorageService fileStorageService;
    @Override
    public SubmissionResponseDTO createSubmission(SubmissionRequestDTO request, MultipartFile file) {
        Student student = studentRepo.findById(request.getStudentId()).orElseThrow(() -> new ResourceNotFoundException("Student not found"));
        Assignment assignment = assignmentRepo.findById(request.getAssignmentId()).orElseThrow(() -> new ResourceNotFoundException("Assignment not found"));
        if (!enrollmentRepo.existsByStudentAndCourse(student, assignment.getCourse())) {
            throw new RuntimeException("Student not enrolled in this course");
        }
        if (submissionRepo.existsByStudentAndAssignment(student, assignment)) {
            throw new DuplicateResourceException("Student already submitted");
        }
        if (assignment.getDueDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Assignment due_date passed");
        }
        String filePath = fileStorageService.uploadFile(file, "Submission");

        Submission submission = new Submission();
        submission.setStatus("SUBMITTED");
        submission.setStudent(student);
        submission.setAssignment(assignment);
        submission.setFileUrl(filePath);
        submission.setFileName(file.getOriginalFilename());
        //save
        // To avoid orphan file
        try{
        Submission saved = submissionRepo.save(submission);
            return mapToResponse(saved);
        }catch (Exception ex) {
            fileStorageService.deleteFile(filePath);
            throw ex;
        }
    }
    @Override
    public SubmissionResponseDTO getSubmissionById(Long submissionId){
        Submission submission=submissionRepo.findById(submissionId).orElseThrow(() -> new ResourceNotFoundException("Submission not found"));
        return mapToResponse(submission);
    }
    @Override
    public List<SubmissionResponseDTO> getAllSubmissions(){
        List<Submission> submissions=submissionRepo.findAll();
        List<SubmissionResponseDTO> response = new ArrayList<>();
        for(Submission submission : submissions){
            response.add(mapToResponse(submission));
        }
        return response;
    }
    @Override
    public List<SubmissionResponseDTO> getSubmissionsByStudent(Long studentId) {
        Student student = studentRepo.findById(studentId).orElseThrow(() ->new ResourceNotFoundException("Student not found"));
        List<Submission> submissions = submissionRepo.findByStudent(student);
        List<SubmissionResponseDTO> response = new ArrayList<>();
        for (Submission submission : submissions) {
            response.add(mapToResponse(submission));
        }
        return response;
    }
    @Override
    public List<SubmissionResponseDTO> getSubmissionsByAssignment(Long assignmentId, String status, String query) {
        Assignment assignment = assignmentRepo.findById(assignmentId).orElseThrow(() -> new ResourceNotFoundException("Assignment not found"));
        List<Submission> submissions = submissionRepo.findSubmissionsByAssignmentWithFilters(assignmentId, status, query);
        List<SubmissionResponseDTO> response = new ArrayList<>();
        for (Submission submission : submissions) {
            response.add(mapToResponse(submission));
        }
        return response;
    }
    @Override
    public void deleteSubmission(Long submissionId){
        Submission submission=submissionRepo.findById(submissionId).orElseThrow(() -> new ResourceNotFoundException("Submission not found"));
        String filepath=submission.getFileUrl();
        fileStorageService.deleteFile(filepath);
        submissionRepo.delete(submission);
    }
    @Override
    public Resource downloadSubmission(Long submissionId) {
        Submission submission = submissionRepo.findById(submissionId).orElseThrow(() -> new ResourceNotFoundException("Submission not found"));
        return fileStorageService.downloadFile(submission.getFileUrl());
    }

    @Override
    public List<SubmissionCenterResponseDTO> getSubmissionCenterByProfessor(Long professorId, Long courseId, String status, String query, Long academicPeriodId) {
        List<Submission> submissions = submissionRepo.findSubmissionCenterByProfessor(professorId, courseId, status, query, academicPeriodId);
        List<SubmissionCenterResponseDTO> response = new ArrayList<>();
        for (Submission submission : submissions) {
            Student student = submission.getStudent();
            Assignment assignment = submission.getAssignment();
            Course course = assignment.getCourse();
            Grade grade = submission.getGrade();

            response.add(SubmissionCenterResponseDTO.builder()
                    .submissionId(submission.getSubmissionId())
                    .courseId(course.getCourseId())
                    .courseCode(course.getCourseCode())
                    .assignmentId(assignment.getAssignmentId())
                    .assignmentTitle(assignment.getTitle())
                    .studentId(student.getStudentId())
                    .studentName(student.getFirstName() + " " + student.getLastName())
                    .registrationNumber(student.getRegistrationNumber())
                    .fileName(submission.getFileName())
                    .fileUrl(submission.getFileUrl())
                    .submittedAt(submission.getSubmittedAt())
                    .status(submission.getStatus())
                    .marks(grade != null ? grade.getMarks() : null)
                    .maxMarks(assignment.getMaxMarks())
                    .build());
        }
        return response;
    }

    private SubmissionResponseDTO mapToResponse(Submission submission){
        return SubmissionResponseDTO.builder()
                .submissionId(submission.getSubmissionId())
                .studentId(submission.getStudent().getStudentId())
                .studentName(submission.getStudent().getFirstName() + " " + submission.getStudent().getLastName())
                .registrationNumber(submission.getStudent().getRegistrationNumber())
                .assignmentId(submission.getAssignment().getAssignmentId())
                .assignmentTitle(submission.getAssignment().getTitle())
                .fileName(submission.getFileName())
                .fileUrl(submission.getFileUrl())
                .submittedAt(submission.getSubmittedAt())
                .status(submission.getStatus())
                .build();
    }
}
