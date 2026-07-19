package edutech.project.dto.request;

import edutech.project.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserRequestDTO {
    private String username;
    private String password;
    @Email(message = "invalid email")
    @NotBlank(message = "email is required")
    private String email;
    @NotNull(message = "role is required")
    private Role role;
    
    private Boolean enabled;
    private Boolean accountLocked;
}
