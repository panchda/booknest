
package com.booknest.auth.config;

import com.booknest.auth.model.Role;
import com.booknest.auth.model.User;
import com.booknest.auth.repository.UserRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Locale;

@Configuration
public class AdminDataInitializer {

    @Bean
    public ApplicationRunner initializeAdmin(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            @Value("${demo.admin.enabled:false}") boolean enabled,
            @Value("${demo.admin.email:}") String email,
            @Value("${demo.admin.password:}") String password
    ) {
        return args -> {
            if (!enabled) {
                return;
            }

            if (email.isBlank() || password.isBlank()) {
                throw new IllegalStateException(
                        "Demo admin email and password must be configured"
                );
            }

            String normalizedEmail = email.trim()
                    .toLowerCase(Locale.ROOT);

            if (userRepository.existsByEmail(normalizedEmail)) {
                System.out.println(
                        "Demo admin already exists: " + normalizedEmail
                );
                return;
            }

            User admin = new User();
            admin.setEmail(normalizedEmail);
            admin.setPassword(passwordEncoder.encode(password));
            admin.setRole(Role.ADMIN);

            userRepository.save(admin);

            System.out.println(
                    "Demo admin created: " + normalizedEmail
            );
        };
    }
}
