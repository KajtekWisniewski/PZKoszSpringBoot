package org.pzkosz.pzkosz.config;

import org.pzkosz.pzkosz.model.PZKoszUser;
import org.pzkosz.pzkosz.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

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

        List<PZKoszUser> users = userService.getAllUsers();
        if (users.isEmpty()) {
            PZKoszUser adminUser = new PZKoszUser();
            adminUser.setUsername("user1");
            adminUser.setEmail("user1@user1.pl");
            adminUser.setPassword(passwordEncoder.encode("user1"));
            adminUser.setRole("ADMIN");

            userService.registerUser(adminUser);
            System.out.println("Admin user 'user1' created successfully.");
        }
    }
}
