package edutech.project.dto.response;

import edutech.project.model.DayOfWeek;
import lombok.Builder;
import lombok.Data;

import java.time.LocalTime;

@Data
@Builder
public class ScheduleResponseDTO {
        private Long scheduleId;
        private Long courseId;
        private String courseName;
        private DayOfWeek day;
        private LocalTime startTime;
        private LocalTime endTime;
        private String roomNumber;
}
