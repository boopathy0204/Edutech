package edutech.project.controller;

import edutech.project.dto.request.CourseRequestDTO;
import edutech.project.dto.response.CourseResponseDTO;
import edutech.project.model.Course;
import edutech.project.service.CourseService;
import jakarta.persistence.PostUpdate;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/course")
public class CourseController {
    @Autowired
    private CourseService courseService;
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSOR')")
    @PostMapping
    public ResponseEntity<CourseResponseDTO> createCourse(@Valid @RequestBody CourseRequestDTO request){
        CourseResponseDTO response=courseService.createCourse(request);
        return new  ResponseEntity<>(response,HttpStatus.CREATED);
    }
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSOR','STUDENT')")
    @GetMapping("/{courseId}/course-details")
    public ResponseEntity<CourseResponseDTO> getCourseById(@PathVariable Long courseId){
        return ResponseEntity.ok(courseService.getCourseById(courseId));
    }
    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT')")
    @GetMapping
    public ResponseEntity<List<CourseResponseDTO>> getAllCourses(
            @RequestParam(value = "query", required = false) String query,
            @RequestParam(value = "academicPeriodId", required = false) Long academicPeriodId) {
        return ResponseEntity.ok(courseService.getAllCourses(query, academicPeriodId));
    }
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSOR')")
    @DeleteMapping("/{courseId}/delete-course")
    public ResponseEntity<String> deleteCourse(@PathVariable Long courseId){
        courseService.deleteCourse(courseId);
        return ResponseEntity.ok("Course deleted successfully");
    }
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSOR')")
    @GetMapping("/{professorId}/course-listbyprofessor")
    public ResponseEntity<List<CourseResponseDTO>> getCoursesByProfessor(
            @PathVariable Long professorId,
            @RequestParam(value = "query", required = false) String query,
            @RequestParam(value = "academicPeriodId", required = false) Long academicPeriodId) {
        return ResponseEntity.ok(courseService.getCoursesByProfessor(professorId, query, academicPeriodId));
    }
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSOR')")
    @PutMapping("/{courseId}/update-course")
    public ResponseEntity<String> updateCourse(@PathVariable Long courseId, @Valid @RequestBody CourseRequestDTO request ){
        courseService.updateCourse(courseId,request);
        return ResponseEntity.ok("Course updated successfully");
    }
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSOR')")
    @PutMapping("/{courseId}/complete")
    public ResponseEntity<String> completeCourse(@PathVariable Long courseId){
        courseService.completeCourse(courseId);
        return ResponseEntity.ok("Course marked as completed");
    }
}
