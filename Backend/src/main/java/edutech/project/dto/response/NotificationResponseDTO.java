package edutech.project.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class NotificationResponseDTO {
    private Long notificationId;
    private String title;
    private String message;
    private Boolean isRead;
    private LocalDateTime createdAt;
}
