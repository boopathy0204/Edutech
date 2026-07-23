package edutech.project.dashboard.service;

import edutech.project.dashboard.dto.*;
import edutech.project.exception.ResourceNotFoundException;
import edutech.project.model.*;
import edutech.project.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import edutech.project.repository.CourseRepo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Service
public class DashboardServiceImpl implements DashboardService {
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private CourseRepo courseRepo;
    @Autowired
    private AssignmentRepo assignmentRepo;
    @Autowired
    private EnrollmentRepo enrollmentRepo;
    @Autowired
    private SubmissionRepo submissionRepo;
    @Autowired
    private GradeRepo gradeRepo;
    @Autowired
    private StudentRepo studentRepo;
    @Autowired
    private ProfessorRepo professorRepo;
    @Autowired
    private edutech.project.repository.AcademicRecordRepo academicRecordRepo;

    @Override
    public Object getDashboard(Long userId, Long academicPeriodId) {
        User user= userRepo.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        switch(user.getRole()){
            case STUDENT:
            return buildStudentDashboard(user, academicPeriodId);
            case PROFESSOR:
            return buildProfessorDashboard(user, academicPeriodId);
            default:
            throw new ResourceNotFoundException("User not found");
        }
    }

    private UserDashboardResponseDTO buildStudentDashboard(User user, Long academicPeriodId) {
        Student student = studentRepo.findByUser(user).orElseThrow(() -> new ResourceNotFoundException("User account has been created successfully, but the student has not completed their profile yet. Please ask the student to complete their registration."));

    UserDashboardResponseDTO response = new UserDashboardResponseDTO();
    StudentAssignmentSummary summary = buildAssignmentSummary(student, academicPeriodId);

    response.setAccount(buildAccount(user));
    response.setStudentProfile(buildStudentProfile(student));
    response.setStatistics(summary.getStatistics());
    response.setCourses(buildStudentCourses(student, academicPeriodId));
    response.setAssignments(summary.getAssignments());

    return response;
    }

    private UserDashboardResponseDTO buildProfessorDashboard(User user, Long academicPeriodId) {
        Professor professor=professorRepo.findByUser(user).orElseThrow(() -> new ResourceNotFoundException("Professor account has been created successfully, but the Professor has not completed their profile yet. Please ask the Professor to complete their registration."));

        UserDashboardResponseDTO response=new UserDashboardResponseDTO();
        ProfessorSummary summary=buildProfessorSummary(professor, academicPeriodId);
        response.setAccount(buildAccount(user));
        response.setProfessorProfile(buildProfessorProfile(professor));
        response.setStatistics(summary.getStatistics());
        response.setCourses(summary.getCourses());
        response.setAssignments(summary.getAssignments());
        return response;

    }

    private AccountDTO buildAccount(User user) {
        return AccountDTO.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .enabled(user.getEnabled())
                .mustChangePassword(user.getMustChangePassword())
                .build();

    }
    private int getGradePoint(String grade) {
        if (grade == null) return 0;
        switch (grade.toUpperCase()) {
            case "O": return 10;
            case "A+": return 9;
            case "A": return 8;
            case "B+": return 7;
            case "B": return 6;
            case "C": return 5;
            case "F": return 0;
            default: return 0;
        }
    }

    private StudentProfileDTO buildStudentProfile(Student student) {
        List<AcademicRecord> records = academicRecordRepo.findByStudent(student);
        
        int earned = 0;
        double totalWeighted = 0;
        int totalCredits = 0;
        
        double currentWeighted = 0;
        int currentCredits = 0;
        
        for (AcademicRecord record : records) {
            String grade = record.getFinalGrade();
            if (grade != null && !grade.equalsIgnoreCase("Not Graded") && !grade.equalsIgnoreCase("In Progress")) {
                int credits = record.getCourse().getCredits();
                int gp = getGradePoint(grade);
                
                if (!grade.equalsIgnoreCase("F")) {
                    earned += credits;
                }
                totalWeighted += credits * gp;
                totalCredits += credits;
                
                if (record.getAcademicYear() != null && record.getAcademicYear().equalsIgnoreCase(student.getAcademicYear())
                        && record.getAcademicHalf() != null && record.getAcademicHalf().equalsIgnoreCase(student.getAcademicHalf())) {
                    currentWeighted += credits * gp;
                    currentCredits += credits;
                }
            }
        }
        
        double cgpa = totalCredits > 0 ? totalWeighted / totalCredits : 0.0;
        double currentGpa = currentCredits > 0 ? currentWeighted / currentCredits : 0.0;

        return StudentProfileDTO.builder()
                .studentId(student.getStudentId())
                .registrationNumber(student.getRegistrationNumber())
                .fullName(student.getFirstName() + " " + student.getLastName())
                .phoneNumber(student.getPhone())
                .department(student.getDepartment())
                .program(student.getProgram())
                .academicYear(student.getAcademicYear())
                .academicHalf(student.getAcademicHalf())
                .currentHalfGpa(currentGpa)
                .cgpa(cgpa)
                .totalCreditsEarned(earned)
                .build();
    }
    private StudentAssignmentSummary buildAssignmentSummary(Student student, Long academicPeriodId) {
        List<AssignmentDashboardDTO> assignmentList = new ArrayList<>();
        int totalAssignments = 0;
        int completedAssignments = 0;
        int pendingAssignments = 0;
        int gradedAssignments = 0;
        int courseCount = 0;
        List<Enrollment> enrollments=enrollmentRepo.findByStudent(student);
        for(Enrollment enrollment : enrollments){
            if (academicPeriodId != null && (enrollment.getAcademicPeriod() == null || !enrollment.getAcademicPeriod().getAcademicPeriodId().equals(academicPeriodId))) {
                continue;
            }
            courseCount++;
            Course course=enrollment.getCourse();
            List<Assignment> assignments=assignmentRepo.findByCourse(course);
            for(Assignment assignment:assignments){
                AssignmentDashboardDTO dto= buildAssignmentDTO(student,assignment);
                assignmentList.add(dto);
                totalAssignments++;
                switch (dto.getSubmissionStatus()) {
                    case "NOT_SUBMITTED":
                        pendingAssignments++;
                        break;
                    case "SUBMITTED":
                        completedAssignments++;
                        break;
                    case "GRADED":
                        completedAssignments++;
                        gradedAssignments++;
                        break;
                }
            }
        }
        StatisticsDTO statistics = StatisticsDTO.builder()
            .totalCourses(courseCount)
            .totalAssignments(totalAssignments)
            .completedAssignments(completedAssignments)
            .pendingAssignments(pendingAssignments)
            .gradedAssignments(gradedAssignments)
            .awaitingGrading(completedAssignments - gradedAssignments)
            .build();

        return StudentAssignmentSummary.builder()
            .statistics(statistics)
            .assignments(assignmentList)
            .build();

    }
    private AssignmentDashboardDTO buildAssignmentDTO(Student student,Assignment assignment){
        Submission submission=submissionRepo.findByStudentAndAssignment(student,assignment).orElse(null);
        String status;
        LocalDateTime submissionDate = null;
        Double marks = null;
        String letterGrade = null;
        if(submission == null){
                status="NOT_SUBMITTED";
        }
        else{
            submissionDate=submission.getSubmittedAt();
            Grade grade=gradeRepo.findBySubmission(submission).orElse(null);
            if(grade ==null){
                status="SUBMITTED";
            }
            else{
                status="GRADED";
                marks=grade.getMarks();
                letterGrade=grade.getLetterGrade();
            }

        }
        return AssignmentDashboardDTO.builder()
        .assignmentId(assignment.getAssignmentId())
        .assignmentTitle(assignment.getTitle())
        .courseName(assignment.getCourse().getCourseName())
        .dueDate(assignment.getDueDate())
        .submissionDate(submissionDate)
        .submissionStatus(status)
        .marksObtained(marks)
        .letterGrade(letterGrade)
        .build();
    }
    private List<CourseDashboardDTO> buildStudentCourses(Student student, Long academicPeriodId){
        List<CourseDashboardDTO> courseList=new ArrayList<>();
        List<Enrollment> enrollments=enrollmentRepo.findByStudent(student);
        for(Enrollment enrollment:enrollments){
            if (academicPeriodId != null && (enrollment.getAcademicPeriod() == null || !enrollment.getAcademicPeriod().getAcademicPeriodId().equals(academicPeriodId))) {
                continue;
            }
            Course course=enrollment.getCourse();
            List<Assignment> assignments=assignmentRepo.findByCourse(course);
            int totalAssignments=assignments.size();
            int submittedAssignments=0;
            for(Assignment assignment:assignments){
                Submission submission = submissionRepo.findByStudentAndAssignment(student, assignment).orElse(null);
                if(submission!=null){
                    submittedAssignments++;
                }
            }
            CourseDashboardDTO dto = CourseDashboardDTO.builder()
                    .courseId(course.getCourseId())
                    .courseCode(course.getCourseCode())
                    .courseName(course.getCourseName())
                    .professorName(course.getProfessor().getName())
                    .enrollmentStatus(enrollment.getStatus())
                    .totalAssignments(totalAssignments)
                    .submittedAssignments(submittedAssignments)
                    .pendingAssignments(totalAssignments - submittedAssignments)
                    .build();
            courseList.add(dto);
        }
        return courseList;
    }

    private ProfessorProfileDTO buildProfessorProfile(Professor professor){
        return ProfessorProfileDTO.builder()
                .professorId(professor.getProfessorId())
                .employeeCode(professor.getEmployeeCode())
                .fullName(professor.getName())
                .department(professor.getDepartment())
                .designation(professor.getDesignation())
                .build();
    }

    private ProfessorSummary buildProfessorSummary(Professor professor, Long academicPeriodId){
        List<CourseDashboardDTO> courses=new ArrayList<>();
        List<AssignmentDashboardDTO> assignments=new ArrayList<>();
        int totalCourses=0;
        int totalAssignments=0;
        int completedGrades=0;
        int pendingGrades=0;
        List<Course> professorCourses=courseRepo.findByProfessor(professor);
        for(Course course:professorCourses){
            if (academicPeriodId != null && (course.getAcademicPeriod() == null || !course.getAcademicPeriod().getAcademicPeriodId().equals(academicPeriodId))) {
                continue;
            }
            totalCourses++;
            List<Enrollment> enrollments=enrollmentRepo.findByCourse(course);
            List<Assignment> assignmentList=assignmentRepo.findByCourse(course);
            int studentCount = enrollments.size();
            courses.add(CourseDashboardDTO.builder()
                            .courseId(course.getCourseId())
                            .courseCode(course.getCourseCode())
                            .courseName(course.getCourseName())
                            .professorName(professor.getName())
                            .totalAssignments(assignmentList.size())
                            .submittedAssignments(0)
                            .pendingAssignments(0)
                            .build());
            totalAssignments +=assignmentList.size();
            for(Assignment assignment : assignmentList){
                int submitted=0;
                int graded=0;
                List<Submission> submissions=submissionRepo.findByAssignment(assignment);
                submitted=submissions.size();
                for(Submission submission : submissions){
                    Grade grade=gradeRepo.findBySubmission(submission).orElse(null);
                    if(grade == null){
                        pendingGrades++;
                    }else{
                        graded++;
                        completedGrades++;
                    }
                }
                assignments.add(AssignmentDashboardDTO.builder()
                                .assignmentId(assignment.getAssignmentId())
                                .assignmentTitle(assignment.getTitle())
                                .courseName(course.getCourseName())
                                .dueDate(assignment.getDueDate())
                                .submissionStatus(graded + "/" + submitted + " Graded")
                                .build());
            }
        }
        StatisticsDTO statistics=StatisticsDTO.builder()
                        .totalCourses(totalCourses)
                        .totalAssignments(totalAssignments)
                        .gradedAssignments(completedGrades)
                        .awaitingGrading(pendingGrades)
                        .build();
        return ProfessorSummary.builder()
                .statistics(statistics)
                .courses(courses)
                .assignments(assignments)
                .build();
    }

}
