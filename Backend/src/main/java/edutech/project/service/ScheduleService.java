package edutech.project.service;

import edutech.project.dto.request.ScheduleRequestDTO;
import edutech.project.dto.response.ScheduleResponseDTO;
import java.util.List;
public interface ScheduleService {
    ScheduleResponseDTO createSchedule(ScheduleRequestDTO request);
    ScheduleResponseDTO getScheduleById(Long scheduleId);
    List<ScheduleResponseDTO> getScheduleByCourse(Long courseId);
    void deleteSchedule(Long scheduleId);
    List<ScheduleResponseDTO> getScheduleByStudent(Long studentId, Long academicPeriodId);
}
