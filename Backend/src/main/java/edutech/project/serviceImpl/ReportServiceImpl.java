package edutech.project.serviceImpl;

import edutech.project.dto.report.AcademicProgressDTO;
import edutech.project.dto.response.AcademicRecordResponseDTO;
import edutech.project.model.Course;
import edutech.project.model.Enrollment;
import edutech.project.model.Grade;
import edutech.project.dto.report.StudentPerformanceDTO;
import edutech.project.dto.report.CourseParticipationDTO;
import edutech.project.model.Student;
import edutech.project.repository.CourseRepo;
import edutech.project.repository.EnrollmentRepo;
import edutech.project.repository.GradeRepo;
import edutech.project.repository.StudentRepo;
import edutech.project.service.AcademicRecordService;
import edutech.project.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ReportServiceImpl implements ReportService {
    @Autowired
    private StudentRepo studentRepo;
    @Autowired
    private GradeRepo gradeRepo;
    @Autowired
    private EnrollmentRepo enrollmentRepo;
    @Autowired
    private AcademicRecordService academicRecordService;
    @Autowired
    private CourseRepo courseRepo;
    @Override
    public StudentPerformanceDTO getStudentPerformanceReport(Long academicPeriodId) {
        List<Student> students;
        List<Grade> grades;
        
        if (academicPeriodId != null) {
            students = enrollmentRepo.findAll().stream()
                .filter(e -> e.getCourse().getAcademicPeriod() != null 
                             && e.getCourse().getAcademicPeriod().getAcademicPeriodId().equals(academicPeriodId))
                .map(Enrollment::getStudent)
                .distinct()
                .toList();
                
            grades = gradeRepo.findAll().stream()
                .filter(g -> g.getSubmission().getAssignment().getCourse().getAcademicPeriod() != null 
                             && g.getSubmission().getAssignment().getCourse().getAcademicPeriod().getAcademicPeriodId().equals(academicPeriodId))
                .toList();
        } else {
            students = studentRepo.findAll();
            grades = gradeRepo.findAll();
        }

        double totalPercentage = 0;
        double highestPercentage = 0;
        double lowestPercentage = grades.isEmpty() ? 0 : 100;
        for (Grade grade : grades) {
            totalPercentage += grade.getPercentage();
            if (grade.getPercentage() > highestPercentage) {
                highestPercentage = grade.getPercentage();
            }
            if (grade.getPercentage() < lowestPercentage) {
                lowestPercentage = grade.getPercentage();
            }
        }
        double averagePercentage = 0;
        if (!grades.isEmpty()) {
            averagePercentage = totalPercentage / grades.size();
        }
        return StudentPerformanceDTO.builder()
                .totalStudents((long) students.size())
                .studentsWithGrades(countStudentsWithGrades(grades))
                .averagePercentage(averagePercentage)
                .highestPercentage(highestPercentage)
                .lowestPercentage(lowestPercentage)
                .build();
    }
    private long countStudentsWithGrades(List<Grade> grades) {
        Set<Long> studentIds = new HashSet<>();
        for (Grade grade : grades) {
            studentIds.add(grade.getSubmission().getStudent().getStudentId());
        }
        return studentIds.size();
    }
    @Override
    public List<CourseParticipationDTO> getCourseParticipationReport(Long academicPeriodId) {
        List<Course> courses = courseRepo.findAll();
        List<CourseParticipationDTO> response = new ArrayList<>();
        for (Course course : courses) {
            if (academicPeriodId != null && (course.getAcademicPeriod() == null || !course.getAcademicPeriod().getAcademicPeriodId().equals(academicPeriodId))) {
                continue;
            }
            List<Enrollment> enrollments = enrollmentRepo.findByCourse(course);
            CourseParticipationDTO dto = CourseParticipationDTO.builder()
                    .courseId(course.getCourseId())
                    .courseName(course.getCourseName())
                    .courseCode(course.getCourseCode())
                    .totalEnrollments(enrollments.size())
                    .academicYear(course.getAcademicYear())
                    .academicHalf(course.getAcademicHalf())
                    .courseStatus(course.getCourseStatus())
                    .professorName(course.getProfessor() != null ? course.getProfessor().getName() : "Not Assigned")
                    .build();
            response.add(dto);
        }
        return response;
    }
    @Override
    public List<AcademicProgressDTO> getAcademicProgressReport(Long academicPeriodId) {
        List<Student> students = studentRepo.findAll();
        List<AcademicProgressDTO> response = new ArrayList<>();
        for (Student student : students) {
            AcademicRecordResponseDTO academicRecord = academicRecordService.getAcademicRecord(student.getStudentId(), academicPeriodId);
            AcademicProgressDTO dto = AcademicProgressDTO.builder()
                            .studentId(student.getStudentId())
                            .studentName(student.getFirstName())
                            .totalCourses(academicRecord.getTotalCourses())
                            .cgpa(academicRecord.getCgpa())
                            .build();
            response.add(dto);
        }
        return response;
    }
}
