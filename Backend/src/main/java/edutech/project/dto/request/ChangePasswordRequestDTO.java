package edutech.project.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ChangePasswordRequestDTO {
    @NotBlank(message = "current password is required")
    private String oldPassword;

    @NotBlank(message = "new password is required")
    @Size(min = 6, message = "new password must be at least 6 characters")
    private String newPassword;
}
