package edutech.project.security.dto;

import edutech.project.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponseDTO {
    private String token;
    private Long userId;
    private String username;
    private Role role;
    private Boolean mustChangePassword;
    private Boolean profileComplete;
}
