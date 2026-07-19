package edutech.project.serviceImpl;

import edutech.project.exception.*;
import edutech.project.repository.AcademicRecordRepo;
import edutech.project.repository.CourseRepo;
import edutech.project.repository.EnrollmentRepo;
import edutech.project.repository.ProfessorRepo;
import edutech.project.service.AcademicPeriodService;
import edutech.project.service.CourseService;
import edutech.project.dto.request.CourseRequestDTO;
import edutech.project.dto.response.CourseResponseDTO;
import edutech.project.model.Course;
import edutech.project.model.Professor;
import edutech.project.model.Student;
import edutech.project.model.Enrollment;
import edutech.project.model.AcademicRecord;
import edutech.project.service.GradeCalculationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service

public class CourseServiceImpl implements CourseService {
    @Autowired
    private  CourseRepo courseRepo;
    @Autowired
    private  ProfessorRepo professorRepo;
    @Autowired
    private AcademicPeriodService academicPeriodService;
    @Autowired
    private EnrollmentRepo enrollmentRepo;
    @Autowired
    private AcademicRecordRepo academicRecordRepo;
    @Autowired
    private GradeCalculationService gradeCalculationService;

    @Override
    public CourseResponseDTO createCourse(CourseRequestDTO request) {
        Professor professor = professorRepo.findById(request.getProfessorId()).orElseThrow(() -> new ResourceNotFoundException("Professor not found"));
        edutech.project.model.AcademicPeriod activePeriod = academicPeriodService.getActivePeriod();
        
        if (courseRepo.existsByCourseCodeAndAcademicPeriod(
                request.getCourseCode(), 
                activePeriod)) {
            throw new DuplicateResourceException("Course Code already exists in this academic period");
        }

        Course course=new Course();
        course.setCourseCode(request.getCourseCode());
        course.setCourseName(request.getCourseName());
        course.setDescription(request.getDescription());
        course.setCredits(request.getCredits());
        course.setProfessor(professor);
        
        course.setAcademicPeriod(activePeriod);
        course.setEnrollmentStartDate(request.getEnrollmentStartDate());
        course.setEnrollmentEndDate(request.getEnrollmentEndDate());
        course.setCourseStartDate(request.getCourseStartDate());
        course.setCourseEndDate(request.getCourseEndDate());
        course.setCourseStatus("UPCOMING");

        // Save
        Course savedCourse = courseRepo.save(course);

        // Return DTO
        return mapToResponse(savedCourse);
    }

    @Override
    public CourseResponseDTO getCourseById(Long courseId) {
        Course course = courseRepo.findById(courseId).orElseThrow(() -> new ResourceNotFoundException("Course not found"));
        return mapToResponse(course);
    }

    @Override
    public List<CourseResponseDTO> getAllCourses(String query, Long academicPeriodId) {
        List<Course> courses;
        if(query ==null||query.isBlank()) {
            courses = courseRepo.findAll();
        }
        else {
            String q = query.trim();
            courses = courseRepo.findByCourseNameContainingIgnoreCaseOrCourseCodeContainingIgnoreCase(q, q);
        }

        List<CourseResponseDTO> courseResponseList = new ArrayList<>();
        for (Course course : courses) {
            if (academicPeriodId == null || (course.getAcademicPeriod() != null && course.getAcademicPeriod().getAcademicPeriodId().equals(academicPeriodId))) {
                courseResponseList.add(mapToResponse(course));
            }
        }
        return courseResponseList;
    }

    @Override
    public void deleteCourse(Long courseId) {
        Course course = courseRepo.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
        courseRepo.delete(course);
    }

    @Override
    public CourseResponseDTO updateCourse(Long courseId, CourseRequestDTO request) {
        Course course = courseRepo.findById(courseId).orElseThrow(() -> new ResourceNotFoundException("Course not found"));
        if ("COMPLETED".equals(course.getCourseStatus())) {
            throw new IllegalStateException("Completed courses cannot be modified.");
        }
        Professor professor = professorRepo.findById(request.getProfessorId()).orElseThrow(() -> new ResourceNotFoundException("Professor not found"));

        if (courseRepo.existsByCourseCodeAndAcademicPeriodAndCourseIdNot(
                request.getCourseCode(), 
                course.getAcademicPeriod(), 
                courseId)) {
            throw new DuplicateResourceException("Course Code already exists in this academic period");
        }
        course.setCourseCode(request.getCourseCode());
        course.setCourseName(request.getCourseName());
        course.setDescription(request.getDescription());
        course.setCredits(request.getCredits());
        course.setProfessor(professor);
        
        course.setEnrollmentStartDate(request.getEnrollmentStartDate());
        course.setEnrollmentEndDate(request.getEnrollmentEndDate());
        course.setCourseStartDate(request.getCourseStartDate());
        course.setCourseEndDate(request.getCourseEndDate());
        
        Course updatedCourse = courseRepo.save(course);
        return mapToResponse(updatedCourse);
    }

    @Override
    public List<CourseResponseDTO> getCoursesByProfessor(Long professorId, String query, Long academicPeriodId) {
        Professor professor = professorRepo.findById(professorId)
                .orElseThrow(() -> new ResourceNotFoundException("Professor not found"));

        List<Course> courses;
        if (query == null || query.isBlank()) {
            courses = courseRepo.findByProfessor(professor);
        } else {
            String q = query.trim();
            courses = courseRepo.findByProfessor_ProfessorIdAndCourseNameContainingIgnoreCaseOrProfessor_ProfessorIdAndCourseCodeContainingIgnoreCase(
                    professorId, q, professorId, q);
        }

        List<CourseResponseDTO> responseList = new ArrayList<>();
        for (Course course : courses) {
            if (academicPeriodId == null || (course.getAcademicPeriod() != null && course.getAcademicPeriod().getAcademicPeriodId().equals(academicPeriodId))) {
                responseList.add(mapToResponse(course));
            }
        }
        return responseList;
    }
    private CourseResponseDTO mapToResponse(Course course) {
        return CourseResponseDTO.builder()
                .courseId(course.getCourseId())
                .courseCode(course.getCourseCode())
                .courseName(course.getCourseName())
                .description(course.getDescription())
                .credits(course.getCredits())
                .professorId(course.getProfessor().getProfessorId())
                .professorName(course.getProfessor().getName())
                .academicYear(course.getAcademicYear())
                .academicHalf(course.getAcademicHalf())
                .enrollmentStartDate(course.getEnrollmentStartDate())
                .enrollmentEndDate(course.getEnrollmentEndDate())
                .courseStartDate(course.getCourseStartDate())
                .courseEndDate(course.getCourseEndDate())
                .courseStatus(course.getCourseStatus())
                .build();
    }
    @Override
    @Transactional
    public void completeCourse(Long courseId) {
        Course course = courseRepo.findById(courseId).orElseThrow(() -> new ResourceNotFoundException("Course not found"));
        if ("COMPLETED".equals(course.getCourseStatus())) {
            return; // Already completed
        }
        course.setCourseStatus("COMPLETED");
        courseRepo.save(course);
        
        // Find enrollments
        List<Enrollment> enrollments = enrollmentRepo.findByCourse(course);
        for (Enrollment enrollment : enrollments) {
            Student student = enrollment.getStudent();
            
            boolean recordExists = academicRecordRepo.existsByStudentAndCourse(student, course);
            if (recordExists) {
                continue;
            }
            
            try {
                // Calculate final grade
                edutech.project.dto.response.FinalGradeDTO finalGrade = gradeCalculationService.calculateFinalGrade(student.getStudentId(), course.getCourseId());
                Integer sem = 0;
                if ("FIRST_HALF".equals(course.getAcademicHalf())) {
                    sem = 1;
                } else if ("SECOND_HALF".equals(course.getAcademicHalf())) {
                    sem = 2;
                }
                
                AcademicRecord record = AcademicRecord.builder()
                        .finalGrade(finalGrade.getFinalLetterGrade())
                        .semester(sem)
                        .academicPeriod(course.getAcademicPeriod())
                        .student(student)
                        .course(course)
                        .build();
                
                academicRecordRepo.save(record);
            } catch (Exception ex) {
                // If student has no grades, do NOT create AcademicRecord (treated as in progress or not graded yet)
                System.out.println("Skipping academic record generation for student: " 
                        + student.getStudentId() + " in course: " + course.getCourseId() + " due to: " + ex.getMessage());
            }
        }
    }
}
