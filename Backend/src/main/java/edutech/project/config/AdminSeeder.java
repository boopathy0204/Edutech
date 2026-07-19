package edutech.project.config;

import edutech.project.model.Role;
import edutech.project.model.User;
import edutech.project.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Creates the very first admin account on startup, if none exists yet.
 * Runs only ONCE - the existsByUsername check means later restarts (even after
 * the admin has changed their password) are a no-op, so the changed password sticks.
 *
 * IMPORTANT: keep app.admin.password out of version control. Set it as an
 * environment variable in your deploy environment, not in a committed
 * application.properties:
 *
 *   app.admin.username=admin
 *   app.admin.password=ChangeMe123!
 *   app.admin.email=admin@yourcollege.edu
 *
 * This password is only ever meant to be used ONCE, to log in and immediately
 * call PATCH /api/user/change-password. mustChangePassword=true blocks every
 * other endpoint until that happens - so even if this default password leaks
 * (e.g. someone finds it in a config file), the only thing they can do with it
 * is log in and be forced straight into the password-change screen.
 */
@Component
public class AdminSeeder implements CommandLineRunner {

    @Autowired
    private UserRepo userRepo;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${app.admin.username}")
    private String adminUsername;
    @Value("${app.admin.password}")
    private String adminPassword;
    @Value("${app.admin.email}")
    private String adminEmail;

    @Override
    public void run(String... args) {
        if (userRepo.existsByUsername(adminUsername)) {
            return;
        }
        User admin = new User();
        admin.setUsername(adminUsername);
        admin.setPassword(passwordEncoder.encode(adminPassword));
        admin.setEmail(adminEmail);
        admin.setRole(Role.ADMIN);
        admin.setMustChangePassword(true);
        userRepo.save(admin);
    }
}