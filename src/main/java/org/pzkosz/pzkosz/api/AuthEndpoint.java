package org.pzkosz.pzkosz.api;

import lombok.RequiredArgsConstructor;
import org.pzkosz.pzkosz.model.PZKoszUser;
import org.pzkosz.pzkosz.security.JwtService;
import org.pzkosz.pzkosz.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthEndpoint {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody PZKoszUser user) {
        try {
            userService.registerUser(user);
            return ResponseEntity.ok("User registered successfully.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error registering user: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestParam String username, @RequestParam String password) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String jwt = jwtService.generateToken(userDetails);

            System.out.println("Generated JWT token: " + jwt);

            return ResponseEntity.ok()
                    .body(new AuthResponse(jwt));
        } catch (Exception e) {
            return ResponseEntity.status(401)
                    .body("Authentication failed: " + e.getMessage());
        }
    }
}

record AuthResponse(String token) {}