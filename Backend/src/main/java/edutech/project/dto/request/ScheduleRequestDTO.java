package edutech.project.dto.request;

import edutech.project.model.DayOfWeek;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalTime;
@Data
public class ScheduleRequestDTO {
    @NotNull(message = "Course Id is required")
    private Long courseId;
    @Enumerated(EnumType.STRING)
    private DayOfWeek day;
    @NotNull(message = "Start time is required")
    private LocalTime startTime;
    @NotNull(message = "End time is required")
    private LocalTime endTime;
    @NotBlank(message = "Room number is required")
    private String roomNumber;

}
