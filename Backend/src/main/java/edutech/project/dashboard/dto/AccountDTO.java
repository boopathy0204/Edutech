package edutech.project.dashboard.dto;

import edutech.project.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountDTO {
    private Long userId;
    private String username;
    private String email;
    private Role role;
    private Boolean enabled;
    private Boolean mustChangePassword;

}