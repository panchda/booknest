package com.booknest.auth.security;

import com.booknest.auth.model.Role;
import com.booknest.auth.model.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;

import static org.junit.jupiter.api.Assertions.*;

import org.springframework.test.util.ReflectionTestUtils;

class JwtServiceTest {

    private static final String TEST_SECRET =
            "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=";

    private static final long EXPIRATION_MS = 3600000L;

    private final JwtService jwtService =
            new JwtService(TEST_SECRET, EXPIRATION_MS);

    @Test
    void shouldGenerateValidJwt() {
        User user = createUser(Role.USER);

        String token = jwtService.generateToken(user);

        assertNotNull(token);
        assertEquals(3, token.split("\\.").length);

        Claims claims = parseClaims(token);

        assertEquals("1", claims.getSubject());
        assertEquals("test@booknest.com", claims.get("email"));
        assertEquals("USER", claims.get("role"));
    }

    @Test
    void shouldIncludeAdminRoleInJwt() {
        User admin = createUser(Role.ADMIN);

        String token = jwtService.generateToken(admin);

        Claims claims = parseClaims(token);

        assertEquals("ADMIN", claims.get("role"));
    }

    @Test
    void shouldSetCorrectExpiration() {
        User user = createUser(Role.USER);

        String token = jwtService.generateToken(user);

        Claims claims = parseClaims(token);

        long actualDuration =
                claims.getExpiration().getTime()
                - claims.getIssuedAt().getTime();

        assertTrue(
                Math.abs(actualDuration - EXPIRATION_MS) < 1000,
                "JWT expiration should be approximately one hour"
        );
    }

    private Claims parseClaims(String token) {
        SecretKey key = Keys.hmacShaKeyFor(
                Decoders.BASE64.decode(TEST_SECRET)
        );

        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private User createUser(Role role) {
        User user = new User();
        ReflectionTestUtils.setField(user, "id", 1L);        user.setEmail("test@booknest.com");
        user.setRole(role);
        return user;
    }
}
