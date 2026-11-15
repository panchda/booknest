package com.booknest.auth.config;

import com.booknest.auth.model.Role;
import com.booknest.auth.model.User;
import com.booknest.auth.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminDataInitializerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ApplicationArguments applicationArguments;

    private AdminDataInitializer initializer;

    @BeforeEach
    void setUp() {
        initializer = new AdminDataInitializer();
    }

    @Test
    void shouldCreateAdminWhenEnabled() throws Exception {
        String email = "Admin@BookNest.Local";
        String password = "demo-password";

        when(userRepository.existsByEmail("admin@booknest.local"))
                .thenReturn(false);

        when(passwordEncoder.encode(password))
                .thenReturn("hashed-password");

        ApplicationRunner runner = initializer.initializeAdmin(
                userRepository,
                passwordEncoder,
                true,
                email,
                password
        );

        runner.run(applicationArguments);

        ArgumentCaptor<User> captor =
                ArgumentCaptor.forClass(User.class);

        verify(userRepository).save(captor.capture());

        User savedAdmin = captor.getValue();

        assertEquals("admin@booknest.local", savedAdmin.getEmail());
        assertEquals(Role.ADMIN, savedAdmin.getRole());
        assertEquals("hashed-password", savedAdmin.getPassword());
    }

    @Test
    void shouldEncodeAdminPasswordBeforeSaving() throws Exception {
        String password = "secure-demo-password";

        when(userRepository.existsByEmail("admin@booknest.local"))
                .thenReturn(false);

        when(passwordEncoder.encode(password))
                .thenReturn("bcrypt-hash");

        ApplicationRunner runner = initializer.initializeAdmin(
                userRepository,
                passwordEncoder,
                true,
                "admin@booknest.local",
                password
        );

        runner.run(applicationArguments);

        verify(passwordEncoder).encode(password);

        ArgumentCaptor<User> captor =
                ArgumentCaptor.forClass(User.class);

        verify(userRepository).save(captor.capture());

        assertEquals("bcrypt-hash", captor.getValue().getPassword());
        assertNotEquals(password, captor.getValue().getPassword());
    }

    @Test
    void shouldNotCreateAdminWhenAlreadyExists() throws Exception {
        when(userRepository.existsByEmail("admin@booknest.local"))
                .thenReturn(true);

        ApplicationRunner runner = initializer.initializeAdmin(
                userRepository,
                passwordEncoder,
                true,
                "admin@booknest.local",
                "demo-password"
        );

        runner.run(applicationArguments);

        verify(userRepository, never()).save(any(User.class));
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void shouldNotCreateAdminWhenDisabled() throws Exception {
        ApplicationRunner runner = initializer.initializeAdmin(
                userRepository,
                passwordEncoder,
                false,
                "admin@booknest.local",
                "demo-password"
        );

        runner.run(applicationArguments);

        verifyNoInteractions(userRepository, passwordEncoder);
    }
}
