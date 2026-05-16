package com.instagram.auth.service;

import com.instagram.auth.dto.LoginRequestDTO;
import com.instagram.auth.dto.LoginResponseDTO;
import com.instagram.auth.entity.User;
import com.instagram.auth.exception.InvalidCredentialsException;
import com.instagram.auth.repository.UserRepository;
import com.instagram.auth.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceLoginTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    private User testUser;
    private LoginRequestDTO loginRequest;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id("user123")
                .fullName("John Doe")
                .email("john@gmail.com")
                .username("johndoe")
                .password("encodedPassword")
                .build();

        loginRequest = LoginRequestDTO.builder()
                .username("johndoe")
                .password("Pass@123")
                .build();
    }

    @Test
    @DisplayName("login() - valid credentials returns token, username, and message")
    void login_withValidCredentials_returnsTokenAndUsername() {
        when(userRepository.findByUsername("johndoe")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("Pass@123", "encodedPassword")).thenReturn(true);
        when(jwtUtil.generateToken("johndoe", "user123")).thenReturn("jwt-token-abc");

        LoginResponseDTO result = authService.login(loginRequest);

        assertNotNull(result);
        assertEquals("jwt-token-abc", result.getToken());
        assertEquals("johndoe", result.getUsername());
        assertEquals("Login successful", result.getMessage());
    }

    @Test
    @DisplayName("login() - wrong password throws InvalidCredentialsException")
    void login_withWrongPassword_throwsInvalidCredentialsException() {
        when(userRepository.findByUsername("johndoe")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("Pass@123", "encodedPassword")).thenReturn(false);

        InvalidCredentialsException exception = assertThrows(
                InvalidCredentialsException.class,
                () -> authService.login(loginRequest)
        );

        assertEquals("Invalid username or password", exception.getMessage());
        verify(jwtUtil, never()).generateToken(anyString(), anyString());
    }

    @Test
    @DisplayName("login() - non-existent username throws InvalidCredentialsException")
    void login_withNonExistentUsername_throwsInvalidCredentialsException() {
        when(userRepository.findByUsername("johndoe")).thenReturn(Optional.empty());

        InvalidCredentialsException exception = assertThrows(
                InvalidCredentialsException.class,
                () -> authService.login(loginRequest)
        );

        assertEquals("Invalid username or password", exception.getMessage());
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(jwtUtil, never()).generateToken(anyString(), anyString());
    }

    @Test
    @DisplayName("login() - verifies password with BCrypt passwordEncoder.matches()")
    void login_verifiesPasswordWithBCrypt() {
        when(userRepository.findByUsername("johndoe")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("Pass@123", "encodedPassword")).thenReturn(true);
        when(jwtUtil.generateToken("johndoe", "user123")).thenReturn("jwt-token-abc");

        authService.login(loginRequest);

        verify(passwordEncoder).matches("Pass@123", "encodedPassword");
    }

    @Test
    @DisplayName("login() - generates JWT token with correct username and userId")
    void login_generatesJwtTokenWithCorrectParams() {
        when(userRepository.findByUsername("johndoe")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("Pass@123", "encodedPassword")).thenReturn(true);
        when(jwtUtil.generateToken("johndoe", "user123")).thenReturn("jwt-token-abc");

        authService.login(loginRequest);

        verify(jwtUtil).generateToken("johndoe", "user123");
    }
}
