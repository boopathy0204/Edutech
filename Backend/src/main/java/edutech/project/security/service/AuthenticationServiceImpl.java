package edutech.project.security.service;

import edutech.project.model.User;
import edutech.project.security.dto.LoginRequestDTO;
import edutech.project.security.dto.LoginResponseDTO;
import edutech.project.security.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private CustomUserDetailsService customUserDetailsService;
    @Autowired
    private JwtService jwtService;

    @Override
    public LoginResponseDTO login(LoginRequestDTO request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(request.getUsername());
        String token = jwtService.generateToken(userDetails);

        User user = (User) userDetails;
        
        boolean complete = true;
        if (user.getRole() == edutech.project.model.Role.STUDENT && user.getStudent() == null) {
            complete = false;
        } else if (user.getRole() == edutech.project.model.Role.PROFESSOR && user.getProfessor() == null) {
            complete = false;
        }

        return LoginResponseDTO.builder()
                .token(token)
                .userId(user.getUserId())
                .username(user.getUsername())
                .role(user.getRole())
                .mustChangePassword(user.getMustChangePassword())
                .profileComplete(complete)
                .build();
    }
}