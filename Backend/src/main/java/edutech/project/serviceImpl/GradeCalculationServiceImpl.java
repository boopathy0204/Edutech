package edutech.project.serviceImpl;

import edutech.project.dto.response.FinalGradeDTO;
import edutech.project.exception.ResourceNotFoundException;
import edutech.project.model.Course;
import edutech.project.model.Grade;
import edutech.project.model.Student;
import edutech.project.repository.CourseRepo;
import edutech.project.repository.GradeRepo;
import edutech.project.repository.StudentRepo;
import edutech.project.service.GradeCalculationService;
import edutech.project.util.GradeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GradeCalculationServiceImpl implements GradeCalculationService {
    @Autowired
    private StudentRepo studentRepo;
    @Autowired
    private CourseRepo courseRepo;
    @Autowired
    private GradeRepo gradeRepo;
    @Autowired
    private  GradeUtil gradeUtil;
    public FinalGradeDTO calculateFinalGrade(Long studentId, Long courseId) {
        Student student =studentRepo.findById(studentId).orElseThrow(() -> new ResourceNotFoundException("Student not found"));
        Course course=courseRepo.findById(courseId).orElseThrow(() ->new ResourceNotFoundException("Course not found"));
        List<Grade> grades =gradeRepo.findBySubmission_StudentAndSubmission_Assignment_Course(student, course);
        if (grades.isEmpty()) {
            throw new ResourceNotFoundException("No grades available for this course.");
        }
        double totalMarks=0;
        double totalPercentage =0;
        for (Grade grade :grades) {
            totalMarks +=grade.getMarks();
            totalPercentage +=grade.getPercentage();
        }
        double averageMarks=totalMarks / grades.size();
        double averagePercentage=totalPercentage / grades.size();
        String finalGrade=gradeUtil.calculateLetterGrade(averagePercentage);
        return FinalGradeDTO.builder()
                .studentId(student.getStudentId())
                .courseId(course.getCourseId())
                .courseName(course.getCourseName())
                .averageMarks(averageMarks)
                .averagePercentage(averagePercentage)
                .finalLetterGrade(finalGrade)
                .build();
    }
}

