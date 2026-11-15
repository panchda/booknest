package com.booknest.auth.service;

import com.booknest.auth.dto.AuthResponse;
import com.booknest.auth.dto.LoginRequest;
import com.booknest.auth.dto.RegisterRequest;
import com.booknest.auth.dto.UserResponse;
import com.booknest.auth.model.Role;
import com.booknest.auth.model.User;
import com.booknest.auth.repository.UserRepository;
import com.booknest.auth.security.JwtService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    @Test
    void shouldRegisterNewUser() {
        RegisterRequest request = new RegisterRequest(
                " TEST@BOOKNEST.COM ",
                "SecurePass123!"
        );

        when(userRepository.existsByEmail("test@booknest.com"))
                .thenReturn(false);

        when(passwordEncoder.encode("SecurePass123!"))
                .thenReturn("hashed-password");

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> {
                    User user = invocation.getArgument(0);
                    ReflectionTestUtils.setField(user, "id", 1L);
                    return user;
                });

        UserResponse response = authService.register(request);

        assertEquals(1L, response.id());
        assertEquals("test@booknest.com", response.email());
        assertEquals(Role.USER, response.role());

        verify(passwordEncoder).encode("SecurePass123!");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldRejectDuplicateEmail() {
        RegisterRequest request = new RegisterRequest(
                "test@booknest.com",
                "SecurePass123!"
        );

        when(userRepository.existsByEmail("test@booknest.com"))
                .thenReturn(true);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> authService.register(request)
        );

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());

        verify(userRepository, never()).save(any(User.class));
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void shouldLoginWithCorrectCredentials() {
        User user = createUser();

        when(userRepository.findByEmail("test@booknest.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(
                "SecurePass123!",
                "hashed-password"
        )).thenReturn(true);

        when(jwtService.generateToken(user))
                .thenReturn("test.jwt.token");

        AuthResponse response = authService.login(
                new LoginRequest(
                        " TEST@BOOKNEST.COM ",
                        "SecurePass123!"
                )
        );

        assertEquals("test.jwt.token", response.accessToken());
        assertEquals("Bearer", response.tokenType());
        assertEquals(3600, response.expiresIn());

        verify(jwtService).generateToken(user);
    }

    @Test
    void shouldRejectLoginWithUnknownEmail() {
        when(userRepository.findByEmail("unknown@booknest.com"))
                .thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> authService.login(
                        new LoginRequest(
                                "unknown@booknest.com",
                                "SecurePass123!"
                        )
                )
        );

        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());

        verifyNoInteractions(passwordEncoder, jwtService);
    }

    @Test
    void shouldRejectLoginWithWrongPassword() {
        User user = createUser();

        when(userRepository.findByEmail("test@booknest.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(
                "WrongPassword",
                "hashed-password"
        )).thenReturn(false);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> authService.login(
                        new LoginRequest(
                                "test@booknest.com",
                                "WrongPassword"
                        )
                )
        );

        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());

        verifyNoInteractions(jwtService);
    }

    private User createUser() {
        User user = new User();
        ReflectionTestUtils.setField(user, "id", 1L);
        user.setEmail("test@booknest.com");
        user.setPassword("hashed-password");
        user.setRole(Role.USER);
        return user;
    }
}
