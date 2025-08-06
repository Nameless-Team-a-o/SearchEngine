package com.nameless.config;

import com.nameless.auth.AuthenticationService;
import com.nameless.entity.user.model.Role;
import com.nameless.entity.user.model.User;
import com.nameless.entity.user.repository.UserRepository;

import com.nameless.jwt.JwtService;
import com.nameless.queue.UserProducer;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class UserCommandRunnerConfig {

    @Bean
    public CommandLineRunner createUser(UserRepository userRepository,
                                        PasswordEncoder passwordEncoder,
                                        JwtService jwtService,
                                        AuthenticationService authService,
                                        UserProducer userProducer) {
        return args -> {
            // Check if the user already exists to avoid duplication
            String initialUsername = "adminUser";
            String initialEmail = "admin@example.com";
            if (userRepository.findByUsername(initialUsername).isEmpty()) {
                // Create the user
                User user = User.builder()
                        .username(initialUsername)
                        .email(initialEmail)
                        .password(passwordEncoder.encode("Admin@123")) // Default password
                        .role(Role.ADMIN) // Assign a role
                        .build();
                userRepository.save(user);

                // Generate JWT tokens for the user
                String accessToken = jwtService.generateToken(user);
                String refreshToken = jwtService.generateRefreshToken(user);

                // Save the refresh token in the database (assume AuthService handles this)
                authService.saveUserToken(user, refreshToken);

                // Send an email
                userProducer.sendUserToQueue(user.getEmail());

                // Print tokens for debugging
                System.out.println("Access Token: " + accessToken);
                System.out.println("Refresh Token: " + refreshToken);
            } else {
                System.out.println("User already exists. Skipping creation.");
            }
        };
    }
}
