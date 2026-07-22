package edutech.project.serviceImpl;

import edutech.project.dto.request.AssignmentRequestDTO;
import edutech.project.dto.response.AssignmentByCourseResponseDTO;
import edutech.project.dto.response.AssignmentResponseDTO;
import edutech.project.exception.DuplicateResourceException;
import edutech.project.exception.ResourceNotFoundException;
import edutech.project.model.*;
import edutech.project.repository.AssignmentRepo;
import edutech.project.repository.CourseRepo;
import edutech.project.repository.EnrollmentRepo;
import edutech.project.service.AssignmentService;
import edutech.project.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class AssignmentServiceImpl implements AssignmentService {
    @Autowired
    private AssignmentRepo assignmentRepo;
    @Autowired
    private CourseRepo courseRepo;
    @Autowired
    private EnrollmentRepo enrollmentRepo;
    @Autowired
    private NotificationService notificationService;

    @Override
    public AssignmentResponseDTO createAssignment(AssignmentRequestDTO request){
        Course course = courseRepo.findById(request.getCourseId()).orElseThrow(()->new ResourceNotFoundException("course not found"));
            if(assignmentRepo.existsByCourseAndTitle(course,request.getTitle())){
                throw new DuplicateResourceException("Title is already exist");
            }
            if((request.getDueDate()).isBefore(LocalDate.now())){
                throw new RuntimeException("Due date need to in future.");
            }

        Assignment assignment= new Assignment();
        assignment.setTitle(request.getTitle());
        assignment.setDescription(request.getDescription());
        assignment.setDueDate(request.getDueDate());
        assignment.setMaxMarks(request.getMaxMarks());
        assignment.setCourse(course);
        Assignment saved= assignmentRepo.save(assignment);
        List<Enrollment> enrollments = enrollmentRepo.findByCourse(saved.getCourse());
        for (Enrollment enrollment : enrollments) {
            notificationService.createNotification(enrollment.getStudent(), "New Assignment", "A new assignment \"" +saved.getTitle() + "\" has been posted.");
        }
        return mapToResponse(saved);
    }
    @Override
    public AssignmentResponseDTO getAssignmentById(Long assignmentId){
        Assignment assignment=assignmentRepo.findById(assignmentId).orElseThrow(()->new ResourceNotFoundException("Assignment not found"));
        return mapToResponse(assignment);
    }
    @Override
    public void deleteAssignment(Long assignmentId){
        Assignment assignment=assignmentRepo.findById(assignmentId).orElseThrow(()->new ResourceNotFoundException("Assignment not found"));
        assignmentRepo.delete(assignment);
    }
    @Override
    public List<AssignmentResponseDTO> getAllAssignment(){
        List<Assignment> assignment=assignmentRepo.findAll();
        List<AssignmentResponseDTO> response=new ArrayList<>();
        for(Assignment ass:assignment){
            response.add(mapToResponse(ass));
        }
        return response;
    }
    @Override
    public AssignmentResponseDTO updateAssignment(Long assignmentId,AssignmentRequestDTO request){
        Assignment assignment=assignmentRepo.findById(assignmentId).orElseThrow(()->new ResourceNotFoundException("Assignment not found"));
        Course course = courseRepo.findById(request.getCourseId()).orElseThrow(()->new ResourceNotFoundException("course not found"));
        if(assignmentRepo.existsByCourseAndTitleAndAssignmentIdNot(course,request.getTitle(),assignmentId)){
            throw new DuplicateResourceException("Title is already exist");
        }
        if((request.getDueDate()).isBefore(LocalDate.now())){
            throw new RuntimeException("Due date need to in future.");
        }
        assignment.setTitle(request.getTitle());
        assignment.setDescription(request.getDescription());
        assignment.setDueDate(request.getDueDate());
        assignment.setMaxMarks(request.getMaxMarks());
        assignment.setCourse(course);
        Assignment saved=assignmentRepo.save(assignment);
        List<Enrollment> enrollments = enrollmentRepo.findByCourse(saved.getCourse());
        for (Enrollment enrollment : enrollments) {
            notificationService.createNotification(enrollment.getStudent(), "Updated Assignment", "A assignment \"" +saved.getTitle() + "\" has been updated.");
        }
         return mapToResponse(saved);
    }
    @Override
    public List<AssignmentByCourseResponseDTO> getAssignmentByCourse(Long courseId){
        Course course = courseRepo.findById(courseId).orElseThrow(()->new ResourceNotFoundException("course not found"));
        List<Assignment> assignments= assignmentRepo.findByCourse(course);
        List<AssignmentByCourseResponseDTO> response=new ArrayList<>();
        for(Assignment ass:assignments){
            response.add(mapToAssignmentByCourseResponse(ass));
        }
        return response;
    }
    private AssignmentByCourseResponseDTO mapToAssignmentByCourseResponse(Assignment assignment){
        return AssignmentByCourseResponseDTO.builder()
                .assignmentId(assignment.getAssignmentId())
                .title(assignment.getTitle())
                .description(assignment.getDescription())
                .dueDate(assignment.getDueDate())
                .maxMarks(assignment.getMaxMarks())
                .courseId(assignment.getCourse().getCourseId())
                .courseCode(assignment.getCourse().getCourseCode())
                .build();
    }
    @Override
    public List<AssignmentResponseDTO> getAssignmentByProfessor(Long professorId, Long courseId, String query, Long academicPeriodId){
            List<Assignment> assignments=assignmentRepo.findAssignmentByProfessor( professorId,courseId,query,academicPeriodId);
            List<AssignmentResponseDTO> response=new ArrayList<>();
            for(Assignment assignment:assignments) {
                response.add(mapToResponse(assignment));
            }
            return response;
    }

    public AssignmentResponseDTO mapToResponse(Assignment assignment){
        return AssignmentResponseDTO.builder()
                .assignmentId(assignment.getAssignmentId())
                .title(assignment.getTitle())
                .description(assignment.getDescription())
                .maxMarks(assignment.getMaxMarks())
                .dueDate(assignment.getDueDate())
                .courseId(assignment.getCourse().getCourseId())
                .courseName(assignment.getCourse().getCourseName())
                .courseCode(assignment.getCourse().getCourseCode())
                .build();
    }
}
