package org.pzkosz.pzkosz.config;

import org.pzkosz.pzkosz.model.PZKoszUser;
import org.pzkosz.pzkosz.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminUserInitializer implements CommandLineRunner {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public AdminUserInitializer(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // Check if the admin user exists
        if (userService.loadUserByUsername("user1") == null) {
            // Create and save the admin user if not exists
            PZKoszUser adminUser = new PZKoszUser();
            adminUser.setUsername("user1");
            adminUser.setEmail("user1@example.com"); // Set a valid email
            adminUser.setPassword(passwordEncoder.encode("user1"));
            adminUser.setRole("ADMIN");

            userService.registerUser(adminUser);
            System.out.println("Admin user 'user1' created successfully.");
        }
    }
}
