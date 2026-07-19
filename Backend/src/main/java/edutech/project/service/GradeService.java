package edutech.project.service;

import edutech.project.dto.request.GradeRequestDTO;
import edutech.project.dto.response.GradeCenterResponseDTO;
import edutech.project.dto.response.GradeResponseDTO;
import org.springframework.stereotype.Service;

import java.util.List;

public interface GradeService {
    GradeResponseDTO createGrade(GradeRequestDTO request);
    GradeResponseDTO getGradeById(Long gradeId);
    List<GradeResponseDTO> getGradeByStudent(Long studentId, Long academicPeriodId);
    List<GradeResponseDTO> getGradeByAssignment(Long assignmentId);
    void deleteGrade (Long gradeId);
    List<GradeResponseDTO> getAllGrades();
    List<GradeCenterResponseDTO> getGradeCenterByProfessor(Long professorId, Long courseId, String query, Long academicPeriodId);
    }
