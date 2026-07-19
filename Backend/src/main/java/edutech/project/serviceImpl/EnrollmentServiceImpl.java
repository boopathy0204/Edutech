package edutech.project.serviceImpl;

import edutech.project.dto.request.EnrollmentRequestDTO;
import edutech.project.dto.response.CourseStudentDTO;
import edutech.project.dto.response.EnrollmentResponseDTO;
import edutech.project.dto.response.StudentCourseDTO;
import edutech.project.exception.DuplicateResourceException;
import edutech.project.exception.ResourceNotFoundException;
import edutech.project.model.*;
import edutech.project.repository.AcademicRecordRepo;
import edutech.project.repository.CourseRepo;
import edutech.project.repository.EnrollmentRepo;
import edutech.project.repository.StudentRepo;
import edutech.project.service.EnrollmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
@Service
public class EnrollmentServiceImpl implements EnrollmentService {

    @Autowired
    private EnrollmentRepo enrollmentRepo;
    @Autowired
    private StudentRepo studentRepo;
    @Autowired
    private CourseRepo courseRepo;
    @Autowired
    private AcademicRecordRepo academicRecordRepo;
    public EnrollmentResponseDTO createEnrollment(EnrollmentRequestDTO request){
        Student student= studentRepo.findById(request.getStudentId()).orElseThrow(()-> new ResourceNotFoundException("student not found"));
        Course course=courseRepo.findById(request.getCourseId()).orElseThrow(()->new ResourceNotFoundException("course not found"));
        
        // Rule 3: Course must not be COMPLETED
        if ("COMPLETED".equalsIgnoreCase(course.getCourseStatus())) {
            throw new IllegalArgumentException("Course is already completed.");
        }
        
        // Rule 2: Current date must be within enrollment start and end dates
        LocalDate now = LocalDate.now();
        if (course.getEnrollmentStartDate() != null && now.isBefore(course.getEnrollmentStartDate())) {
            throw new IllegalArgumentException("Enrollment has not started yet.");
        }
        if (course.getEnrollmentEndDate() != null && now.isAfter(course.getEnrollmentEndDate())) {
            throw new IllegalArgumentException("Enrollment period has ended.");
        }


        // Rule 5: Student cannot enroll in the same course twice during the same Academic Half
        List<Enrollment> enrollments = enrollmentRepo.findByStudent(student);
        boolean alreadyEnrolledInCode = false;
        int currentCredits = 0;

        for (Enrollment e : enrollments) {
            // Check duplicate enrollment
            if (e.getCourse().getCourseCode().equalsIgnoreCase(course.getCourseCode())
                    && e.getCourse().getAcademicPeriod() != null && course.getAcademicPeriod() != null
                    && e.getCourse().getAcademicPeriod().getAcademicPeriodId().equals(course.getAcademicPeriod().getAcademicPeriodId())) {
                alreadyEnrolledInCode = true;
            }
            // Calculate current semester credits
            if (e.getCourse().getAcademicPeriod() != null && course.getAcademicPeriod() != null
                    && e.getCourse().getAcademicPeriod().getAcademicPeriodId().equals(course.getAcademicPeriod().getAcademicPeriodId())) {
                currentCredits += e.getCourse().getCredits();
            }
        }

        if (alreadyEnrolledInCode) {
            throw new DuplicateResourceException("You are already enrolled in this course for this term.");
        }

// Rule 6: Pre-existing pass check in AcademicRecord
        List<AcademicRecord> records = academicRecordRepo.findByStudent(student);
        boolean alreadyPassed = false;

        for (AcademicRecord r : records) {
            if (r.getCourse().getCourseCode().equalsIgnoreCase(course.getCourseCode())
                    && r.getFinalGrade() != null
                    && !r.getFinalGrade().equalsIgnoreCase("F")
                    && !r.getFinalGrade().equalsIgnoreCase("Not Graded")) {
                alreadyPassed = true;
                break;
            }
        }

        if (alreadyPassed) {
            throw new IllegalArgumentException("You have already completed this course.");
        }

// currentCredits now contains the total credits for the current academic period.
        if (currentCredits + course.getCredits() > 20) {
            throw new IllegalArgumentException("Enrolling in this course would exceed the maximum limit of 20 credits.");
        }
        Enrollment enrollment=new Enrollment();
        enrollment.setStudent(student);
        enrollment.setCourse(course);
        enrollment.setAcademicPeriod(course.getAcademicPeriod());
        enrollment.setStatus(EnrollmentStatus.ACTIVE);
        Enrollment saved = enrollmentRepo.save(enrollment);
        return maptoResponse(saved);
    }
    @Override
    public EnrollmentResponseDTO getEnrollmentById(Long enrollmentId){
        Enrollment enrollment = enrollmentRepo.findById(enrollmentId).orElseThrow(()-> new ResourceNotFoundException("Resource not found"));
        return maptoResponse(enrollment);
    }
     @Override
     public List<EnrollmentResponseDTO> getAllEnrollment(){
        List<Enrollment> enrollments = enrollmentRepo.findAll();
        List<EnrollmentResponseDTO> response = new ArrayList<>();
        for(Enrollment enrollment : enrollments){
            response.add(maptoResponse(enrollment));
        }
        return response;
     }
     @Override
     public void deleteEnrollment(Long enrollmentId){
         Enrollment enrollment = enrollmentRepo.findById(enrollmentId).orElseThrow(()-> new ResourceNotFoundException("Resource not found"));
         enrollmentRepo.delete(enrollment);
     }
     @Override
     public EnrollmentResponseDTO updateEnrollmentStatus(Long enrollmentId,String status){
         Enrollment enrollment = enrollmentRepo.findById(enrollmentId).orElseThrow(()-> new ResourceNotFoundException("Resource not found"));
         if(status.equalsIgnoreCase("Active")) {
             enrollment.setStatus(EnrollmentStatus.ACTIVE);
         }
         else if (status.equalsIgnoreCase("Dropped")) {
             enrollment.setStatus(EnrollmentStatus.DROPPED);
         }
         else if(status.equalsIgnoreCase("Completed")){
             enrollment.setStatus(EnrollmentStatus.COMPLETED);
         }
         else{
             throw new IllegalArgumentException("Invalid Status");
         }
         Enrollment saved=enrollmentRepo.save(enrollment);
         return maptoResponse(saved);
     }
    @Override
    public List<StudentCourseDTO> getCoursesByStudent(Long studentId, String query, Long academicPeriodId){
        Student student = studentRepo.findById(studentId).orElseThrow(()-> new ResourceNotFoundException("Student not found"));
        List<Enrollment> enrollments = enrollmentRepo.findByStudent(student);

        if (query != null && !query.isBlank()) {
            String q = query.trim().toLowerCase();
            List<Enrollment> filtered = new ArrayList<>();
            for (Enrollment enrollment : enrollments) {
                String courseName = enrollment.getCourse().getCourseName().toLowerCase();
                String courseCode = enrollment.getCourse().getCourseCode().toLowerCase();
                if (courseName.contains(q) || courseCode.contains(q)) {
                    filtered.add(enrollment);
                }
            }
            enrollments = filtered;
        }

        List<StudentCourseDTO> courses=new ArrayList<>();
        for(Enrollment enrollment:enrollments){
            if (academicPeriodId == null || (enrollment.getAcademicPeriod() != null && enrollment.getAcademicPeriod().getAcademicPeriodId().equals(academicPeriodId))) {
                courses.add(maptoStudentCourse(enrollment));
            }
        }
        return courses;
    }
    @Override
    public List<CourseStudentDTO> getStudentsByCourse(Long courseId, String query){
        Course course = courseRepo.findById(courseId).orElseThrow(()-> new ResourceNotFoundException("Course not found"));
        List<Enrollment> enrollments = enrollmentRepo.findByCourse(course);
        
        if (query != null && !query.isBlank()) {
            String q = query.trim().toLowerCase();
            List<Enrollment> filtered = new ArrayList<>();
            for (Enrollment enrollment : enrollments) {
                String firstName = enrollment.getStudent().getFirstName().toLowerCase() ;
                String lastName = enrollment.getStudent().getLastName() != null ? enrollment.getStudent().getLastName().toLowerCase() : "";
                String fullName = firstName + " " + lastName;
                String regNumber = enrollment.getStudent().getRegistrationNumber();
                if (firstName.contains(q) || lastName.contains(q) || fullName.contains(q) || regNumber.contains(q)) {
                    filtered.add(enrollment);
                }
            }
            enrollments = filtered;
        }

        List<CourseStudentDTO> student = new ArrayList<>();
        for(Enrollment enrollment:enrollments){
            student.add(maptoCourseStudent(enrollment));
        }
        return student;
    }
    private CourseStudentDTO maptoCourseStudent(Enrollment enrollment){
        return CourseStudentDTO.builder()
                .studentId(enrollment.getStudent().getStudentId())
                .studentName(enrollment.getStudent().getFirstName())
                .status(String.valueOf(enrollment.getStatus()))
                .enrollmentDate(enrollment.getEnrollmentDate())
                .registrationNumber(enrollment.getStudent().getRegistrationNumber())
                .build();
    }
    private StudentCourseDTO maptoStudentCourse(Enrollment enrollment){
        return StudentCourseDTO.builder()
                .courseId(enrollment.getCourse().getCourseId())
                .courseName(enrollment.getCourse().getCourseName())
                .status(String.valueOf(enrollment.getStatus()))
                .enrollmentDate(enrollment.getEnrollmentDate())
                .courseCode(enrollment.getCourse().getCourseCode())
                .description(enrollment.getCourse().getDescription())
                .credits(enrollment.getCourse().getCredits())
                .professorName(enrollment.getCourse().getProfessor().getName())
                .build();
    }
    private EnrollmentResponseDTO maptoResponse(Enrollment enrollment){
        return EnrollmentResponseDTO.builder()
                .enrollmentId(enrollment.getEnrollmentId())
                .studentId(enrollment.getStudent().getStudentId())
                .studentName(enrollment.getStudent().getFirstName())
                .courseId(enrollment.getCourse().getCourseId())
                .courseName(enrollment.getCourse().getCourseName())
                .enrollmentDate((enrollment.getEnrollmentDate()))
                .status(String.valueOf(enrollment.getStatus())).build();
    }

}
