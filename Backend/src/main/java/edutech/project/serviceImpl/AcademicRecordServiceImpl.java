package edutech.project.serviceImpl;

import edutech.project.dto.response.AcademicRecordResponseDTO;
import edutech.project.dto.response.CourseRecordDTO;
import edutech.project.dto.response.FinalGradeDTO;
import edutech.project.exception.ResourceNotFoundException;
import edutech.project.model.Course;
import edutech.project.model.Enrollment;
import edutech.project.model.Student;
import edutech.project.model.AcademicRecord;
import edutech.project.repository.AcademicRecordRepo;
import edutech.project.repository.EnrollmentRepo;
import edutech.project.repository.StudentRepo;
import edutech.project.service.AcademicRecordService;
import edutech.project.service.GradeCalculationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AcademicRecordServiceImpl implements AcademicRecordService {
    @Autowired
    private StudentRepo studentRepo;
    @Autowired
    private EnrollmentRepo enrollmentRepo;
    @Autowired
    private GradeCalculationService gradeCalculationService;
    @Autowired
    private AcademicRecordRepo academicRecordRepo;
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

    @Override
    public AcademicRecordResponseDTO getAcademicRecord(Long studentId, Long academicPeriodId) {
        Student student = studentRepo.findById(studentId).orElseThrow(() -> new ResourceNotFoundException("Student not found"));
        List<AcademicRecord> records = academicRecordRepo.findByStudent(student);
        List<CourseRecordDTO> courseRecords = new ArrayList<>();
        
        double totalWeightedPoints = 0;
        int totalCredits = 0;
        
        for (AcademicRecord record : records) {
            if (academicPeriodId != null && (record.getAcademicPeriod() == null || !record.getAcademicPeriod().getAcademicPeriodId().equals(academicPeriodId))) {
                continue;
            }
            Course course = record.getCourse();
            Double avgMarks = null;
            Double avgPercent = null;
            try {
                FinalGradeDTO finalGrade = gradeCalculationService.calculateFinalGrade(student.getStudentId(), course.getCourseId());
                avgMarks = finalGrade.getAverageMarks();
                avgPercent = finalGrade.getAveragePercentage();
            } catch (Exception ex) {
                // Ignore and keep null
            }
            
            CourseRecordDTO courseRecord = CourseRecordDTO.builder()
                    .courseId(course.getCourseId())
                    .courseName(course.getCourseName())
                    .professorName(course.getProfessor().getName())
                    .enrollmentStatus("COMPLETED")
                    .averageMarks(avgMarks)
                    .averagePercentage(avgPercent)
                    .finalGrade(record.getFinalGrade())
                    .academicYear(record.getAcademicYear())
                    .academicHalf(record.getAcademicHalf())
                    .build();
            courseRecords.add(courseRecord);
            
            String grade = record.getFinalGrade();
            if (grade != null && !grade.equalsIgnoreCase("Not Graded") && !grade.equalsIgnoreCase("In Progress")) {
                int credits = course.getCredits();
                int gp = getGradePoint(grade);
                totalWeightedPoints += credits * gp;
                totalCredits += credits;
            }
        }
        
        double cgpa = 0;
        if (totalCredits > 0) {
            cgpa = totalWeightedPoints / totalCredits;
        }
        
        long totalCourses = enrollmentRepo.findByStudent(student).stream()
                .filter(e -> academicPeriodId == null || (e.getAcademicPeriod() != null && e.getAcademicPeriod().getAcademicPeriodId().equals(academicPeriodId)))
                .count();

        return AcademicRecordResponseDTO.builder()
                .studentId(student.getStudentId())
                .studentName(student.getFirstName())
                .totalCourses((int) totalCourses)
                .cgpa(cgpa)
                .courses(courseRecords)
                .build();
    }
}
