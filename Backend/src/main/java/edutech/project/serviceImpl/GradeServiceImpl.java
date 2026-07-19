package edutech.project.serviceImpl;

import edutech.project.dto.request.GradeRequestDTO;
import edutech.project.dto.response.GradeCenterResponseDTO;
import edutech.project.dto.response.GradeResponseDTO;
import edutech.project.exception.DuplicateResourceException;
import edutech.project.exception.ResourceNotFoundException;
import edutech.project.model.*;
import edutech.project.repository.AssignmentRepo;
import edutech.project.repository.GradeRepo;
import edutech.project.repository.StudentRepo;
import edutech.project.repository.SubmissionRepo;
import edutech.project.service.GradeService;

import java.util.ArrayList;
import java.util.List;

import edutech.project.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GradeServiceImpl implements GradeService {
    @Autowired
    private GradeRepo gradeRepo;
    @Autowired
    private SubmissionRepo submissionRepo;
    @Autowired
    private StudentRepo studentRepo;
    @Autowired
    private AssignmentRepo assignmentRepo;
    @Autowired
    private NotificationService notificationService;
    @Override
    public GradeResponseDTO createGrade(GradeRequestDTO request){
        Submission submission=submissionRepo.findById(request.getSubmissionId()).orElseThrow(()->new ResourceNotFoundException("submission not found"));
        if(gradeRepo.existsBySubmission(submission)){
            throw new DuplicateResourceException("Already Graded");
        }
        if (request.getMarks() > submission.getAssignment().getMaxMarks()) {
            throw new IllegalArgumentException("Marks cannot be greater than maximum marks");
        }
        Grade grade=new Grade();

        Double percentage = percentage(request.getMarks(), submission);
        grade.setMarks(request.getMarks());
        grade.setPercentage(percentage);
        grade.setLetterGrade(letterGrade(percentage));
        grade.setFeedback(request.getFeedback());
        grade.setSubmission(submission);
        //save
        submission.setStatus("GRADED");
        submissionRepo.save(submission);
        Grade saved=gradeRepo.save(grade);
        notificationService.createNotification(submission.getStudent(), "Assignment Graded", "Your assignment \"" + submission.getAssignment().getTitle() + "\" has been graded.");
         return mapToResponse(saved);
    }
    private Double percentage(Double mark,Submission submission){
        Integer maxMark=submission.getAssignment().getMaxMarks();
        return mark/maxMark*100;
    }
    private String letterGrade(Double percentage){
        if(percentage>=90){
            return "O";
        }
        if (percentage >=80) {
            return "A";
        }
        if(percentage>=70){
            return "B";
        }
        if(percentage>=60){
            return "C";
        }
        return "D";
    }
    @Override
    public GradeResponseDTO getGradeById(Long gradeId) {
        Grade grade = gradeRepo.findById(gradeId).orElseThrow(() -> new ResourceNotFoundException("Grade not found"));
        return mapToResponse(grade);
    }
    @Override
    public List<GradeResponseDTO> getAllGrades() {
        List<Grade> grades = gradeRepo.findAll();
        List<GradeResponseDTO> response = new ArrayList<>();
        for (Grade grade : grades) {
            response.add(mapToResponse(grade));
        }
        return response;
    }
    @Override
    public List<GradeResponseDTO> getGradeByStudent(Long studentId, Long academicPeriodId) {
        Student student = studentRepo.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
        List<Grade> grades = gradeRepo.findBySubmission_Student(student);
        List<GradeResponseDTO> response = new ArrayList<>();
        for (Grade grade : grades) {
            Course course = grade.getSubmission().getAssignment().getCourse();
            if (academicPeriodId == null || (course.getAcademicPeriod() != null && course.getAcademicPeriod().getAcademicPeriodId().equals(academicPeriodId))) {
                response.add(mapToResponse(grade));
            }
        }
        return response;
    }
    @Override
    public List<GradeResponseDTO> getGradeByAssignment(Long assignmentId) {
        Assignment assignment = assignmentRepo.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found"));
        List<Grade> grades = gradeRepo.findBySubmission_Assignment(assignment);
        List<GradeResponseDTO> response = new ArrayList<>();
        for (Grade grade : grades) {
            response.add(mapToResponse(grade));
        }
        return response;
    }
    @Override
    public void deleteGrade(Long gradeId) {
        Grade grade = gradeRepo.findById(gradeId).orElseThrow(() ->new ResourceNotFoundException("Grade not found"));
        gradeRepo.delete(grade);
    }

    @Override
    public List<GradeCenterResponseDTO> getGradeCenterByProfessor(Long professorId, Long courseId, String query, Long academicPeriodId) {
        List<Grade> grades = gradeRepo.findGradeCenterByProfessor(professorId, courseId, query);
        List<GradeCenterResponseDTO> response = new ArrayList<>();
        for (Grade grade : grades) {
            Submission submission = grade.getSubmission();
            Assignment assignment = submission.getAssignment();
            Course course = assignment.getCourse();
            if (academicPeriodId != null && (course.getAcademicPeriod() == null || !course.getAcademicPeriod().getAcademicPeriodId().equals(academicPeriodId))) {
                continue;
            }
            response.add(GradeCenterResponseDTO.builder()
                    .gradeId(grade.getGradeId())
                    .courseId(course.getCourseId())
                    .courseCode(course.getCourseCode())
                    .courseName(course.getCourseName())
                    .assignmentId(assignment.getAssignmentId())
                    .assignmentTitle(assignment.getTitle())
                    .maxMarks(assignment.getMaxMarks())
                    .studentId(submission.getStudent().getStudentId())
                    .studentName(submission.getStudent().getFirstName() + " " + submission.getStudent().getLastName())
                    .registrationNumber(submission.getStudent().getRegistrationNumber())
                    .marks(grade.getMarks())
                    .percentage(grade.getPercentage())
                    .letterGrade(grade.getLetterGrade())
                    .feedback(grade.getFeedback())
                    .gradedAt(grade.getGradedAt())
                    .build());
        }
        return response;
    }
    private GradeResponseDTO mapToResponse(Grade grade) {
        return GradeResponseDTO.builder()
                .gradeId(grade.getGradeId())
                .submissionId(grade.getSubmission().getSubmissionId())
                .studentId(grade.getSubmission().getStudent().getStudentId())
                .studentName(grade.getSubmission().getStudent().getFirstName() + " " + grade.getSubmission().getStudent().getLastName())
                .registrationNumber(grade.getSubmission().getStudent().getRegistrationNumber())
                .assignmentId(grade.getSubmission().getAssignment().getAssignmentId())
                .assignmentTitle(grade.getSubmission().getAssignment().getTitle())
                .marks(grade.getMarks())
                .percentage(grade.getPercentage())
                .letterGrade(grade.getLetterGrade())
                .feedback(grade.getFeedback())
                .gradedAt(grade.getGradedAt())
                .maxmark(grade.getSubmission().getAssignment().getMaxMarks())
                .build();
    }
}
