package edutech.project.serviceImpl;

import edutech.project.model.AcademicPeriod;
import edutech.project.model.Course;
import edutech.project.model.Student;
import edutech.project.repository.AcademicPeriodRepo;
import edutech.project.repository.CourseRepo;
import edutech.project.repository.StudentRepo;
import edutech.project.service.AcademicPeriodService;
import edutech.project.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class AcademicPeriodServiceImpl implements AcademicPeriodService {

    @Autowired
    private AcademicPeriodRepo academicPeriodRepo;
    @Autowired
    private StudentRepo studentRepo;
    @Autowired
    private CourseRepo courseRepo;
    @Autowired
    @Lazy
    private CourseService courseService;

    @Override
    public List<AcademicPeriod> getAllPeriods() {
        return academicPeriodRepo.findAll();
    }

    @Override
    public AcademicPeriod getActivePeriod() {
        return academicPeriodRepo.findFirstByStatus("ACTIVE").orElseGet(() -> {
                    // Create default active period if not exists
                    AcademicPeriod period = AcademicPeriod.builder()
                            .academicYear("2026-2027")
                            .academicHalf("FIRST_HALF")
                            .status("ACTIVE")
                            .startDate(LocalDate.of(2026, 7, 1))
                            .endDate(LocalDate.of(2026, 12, 31))
                            .build();
                    return academicPeriodRepo.save(period);
                });
    }

    @Override
    public AcademicPeriod getPeriodById(Long id) {
        return academicPeriodRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("Academic period not found with ID: " + id));
    }

    @Override
    public AcademicPeriod createPeriod(String year, String half, String status) {
        AcademicPeriod period = AcademicPeriod.builder()
                .academicYear(year)
                .academicHalf(half)
                .status(status)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusMonths(6))
                .build();
        return academicPeriodRepo.save(period);
    }

    @Override
    @Transactional
    public void advancePeriod() {
        AcademicPeriod currentPeriod = getActivePeriod();
        
        String currentYear = currentPeriod.getAcademicYear();
        String currentHalf = currentPeriod.getAcademicHalf();
        
        String nextYear = currentYear;
        String nextHalf = "FIRST_HALF";
        
        if ("FIRST_HALF".equalsIgnoreCase(currentHalf)) {
            nextHalf = "SECOND_HALF";
        } else {

            String[] parts = currentYear.split("-");
            int year1 = Integer.parseInt(parts[0]);
            int year2 = Integer.parseInt(parts[1]);
            nextYear = (year1 + 1) + "-" + (year2 + 1);
            nextHalf = "FIRST_HALF";
        }
        
        // Complete current courses
        List<Course> activeCourses = courseRepo.findByAcademicPeriod(currentPeriod);
        for (Course course : activeCourses) {
            try {
                courseService.completeCourse(course.getCourseId());
            } catch (Exception ex) {
                System.out.println("Error completing course " + course.getCourseId() + ": " + ex.getMessage());
            }
        }
        
        // Close current period
        currentPeriod.setStatus("CLOSED");
        academicPeriodRepo.save(currentPeriod);

        final String targetYear = nextYear;
        final String targetHalf = nextHalf;
        AcademicPeriod nextPeriod = academicPeriodRepo.findByAcademicYearAndAcademicHalf(targetYear, targetHalf)
                .orElseGet(() -> {
                    AcademicPeriod period = AcademicPeriod.builder()
                            .academicYear(targetYear)
                            .academicHalf(targetHalf)
                            .status("ACTIVE")
                            .startDate(LocalDate.now())
                            .endDate(LocalDate.now().plusMonths(6))
                            .build();
                    return academicPeriodRepo.save(period);
                });
        
        nextPeriod.setStatus("ACTIVE");
        academicPeriodRepo.save(nextPeriod);
        
        // Advance all active students to next period
        List<Student> students = studentRepo.findAll();
        for (Student student : students) {
            student.setAcademicPeriod(nextPeriod);
            studentRepo.save(student);
        }
    }
}
