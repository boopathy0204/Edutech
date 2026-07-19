package edutech.project.service;

import edutech.project.dto.response.AcademicRecordResponseDTO;

public interface AcademicRecordService {
    AcademicRecordResponseDTO getAcademicRecord(Long studentId, Long academicPeriodId);
}
