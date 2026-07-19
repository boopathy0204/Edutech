package edutech.project.service;

import edutech.project.dto.response.FinalGradeDTO;

public interface GradeCalculationService {
    FinalGradeDTO calculateFinalGrade(Long studentId, Long courseId);
}
