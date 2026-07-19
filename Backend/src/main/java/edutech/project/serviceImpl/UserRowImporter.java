package edutech.project.serviceImpl;

import edutech.project.dto.response.BulkImportRowResultDTO;
import edutech.project.exception.DuplicateResourceException;
import edutech.project.model.Role;
import edutech.project.model.User;
import edutech.project.repository.UserRepo;
import edutech.project.util.CredentialGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class UserRowImporter {
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Transactional
    public BulkImportRowResultDTO importRow(String name, String email, String roleStr, int rowNumber) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required in row " + rowNumber);
        }
        if (roleStr == null || roleStr.trim().isEmpty()) {
            throw new IllegalArgumentException("Role is required in row " + rowNumber);
        }

        Role role;
        try {
            role = Role.valueOf(roleStr.toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid role: " + roleStr + " in row " + rowNumber + ". Allowed values are ADMIN, PROFESSOR, STUDENT");
        }

        if (userRepo.existsByEmail(email)) {
            throw new DuplicateResourceException("Email '" + email + "' already exists");
        }

        String baseUsername = CredentialGenerator.generateUsername(email);
        String username = baseUsername;
        int count = 1;
        while (userRepo.existsByUsername(username)) {
            username = baseUsername + count;
            count++;
        }
        String tempPassword = CredentialGenerator.generateTempPassword();

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(tempPassword));
        user.setRole(role);
        user.setMustChangePassword(true);
        userRepo.save(user);

        return BulkImportRowResultDTO.builder()
                .rowNumber(rowNumber)
                .success(true)
                .message("Created")
                .generatedUsername(username)
                .generatedPassword(tempPassword)
                .name(name)
                .email(email)
                .role(role.name())
                .build();
    }
}
