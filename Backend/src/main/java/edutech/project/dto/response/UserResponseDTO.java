package edutech.project.dto.response;

import edutech.project.model.Role;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponseDTO {
    private Long userId;
    private String username;
    private String email;
    private Role role;
    private Boolean enabled;
    private String temporaryPassword;
    private Boolean mustChangePassword;
    private Boolean profileComplete;

}

