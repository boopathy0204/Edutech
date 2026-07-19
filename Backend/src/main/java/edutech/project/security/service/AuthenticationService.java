package edutech.project.security.service;

import edutech.project.security.dto.LoginRequestDTO;
import edutech.project.security.dto.LoginResponseDTO;

public interface AuthenticationService {
    LoginResponseDTO login(LoginRequestDTO request);

}
