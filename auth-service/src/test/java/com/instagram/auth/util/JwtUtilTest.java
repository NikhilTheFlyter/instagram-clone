package com.instagram.auth.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    private static final String SECRET = "test-secret-key-that-is-at-least-256-bits-long-for-hmac-sha";
    private static final long EXPIRATION_MS = 86400000L;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil(SECRET, EXPIRATION_MS);
    }

    @Test
    @DisplayName("generateToken() - returns a non-null token")
    void generateToken_returnsNonNullToken() {
        String token = jwtUtil.generateToken("johndoe", "user123");

        assertNotNull(token);
        assertFalse(token.isBlank());
    }

    @Test
    @DisplayName("generateToken() - token contains correct username")
    void generateToken_tokenContainsUsername() {
        String token = jwtUtil.generateToken("johndoe", "user123");

        String extractedUsername = jwtUtil.extractUsername(token);

        assertEquals("johndoe", extractedUsername);
    }

    @Test
    @DisplayName("generateToken() - token contains correct userId")
    void generateToken_tokenContainsUserId() {
        String token = jwtUtil.generateToken("johndoe", "user123");

        String extractedUserId = jwtUtil.extractUserId(token);

        assertEquals("user123", extractedUserId);
    }

    @Test
    @DisplayName("validateToken() - valid token returns true")
    void validateToken_withValidToken_returnsTrue() {
        String token = jwtUtil.generateToken("johndoe", "user123");

        assertTrue(jwtUtil.validateToken(token));
    }

    @Test
    @DisplayName("validateToken() - invalid token returns false")
    void validateToken_withInvalidToken_returnsFalse() {
        assertFalse(jwtUtil.validateToken("this.is.not.a.valid.jwt"));
    }

    @Test
    @DisplayName("validateToken() - expired token returns false")
    void validateToken_withExpiredToken_returnsFalse() throws InterruptedException {
        JwtUtil shortLivedJwtUtil = new JwtUtil(SECRET, 1L);

        String token = shortLivedJwtUtil.generateToken("johndoe", "user123");

        Thread.sleep(50);

        assertFalse(shortLivedJwtUtil.validateToken(token));
    }

    @Test
    @DisplayName("extractUsername() - returns correct username")
    void extractUsername_returnsCorrectUsername() {
        String token = jwtUtil.generateToken("testuser", "id456");

        assertEquals("testuser", jwtUtil.extractUsername(token));
    }

    @Test
    @DisplayName("extractUserId() - returns correct userId")
    void extractUserId_returnsCorrectUserId() {
        String token = jwtUtil.generateToken("testuser", "id456");

        assertEquals("id456", jwtUtil.extractUserId(token));
    }
}
