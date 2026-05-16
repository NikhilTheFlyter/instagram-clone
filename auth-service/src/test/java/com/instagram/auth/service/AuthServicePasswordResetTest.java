package com.instagram.auth.service;

import com.instagram.auth.dto.ForgotPasswordRequestDTO;
import com.instagram.auth.dto.ResetPasswordRequestDTO;
import com.instagram.auth.entity.User;
import com.instagram.auth.exception.InvalidCredentialsException;
import com.instagram.auth.exception.InvalidResetTokenException;
import com.instagram.auth.exception.UserNotFoundException;
import com.instagram.auth.repository.UserRepository;
import com.instagram.auth.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServicePasswordResetTest {

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

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id("user123")
                .fullName("John Doe")
                .email("john@gmail.com")
                .username("johndoe")
                .password("encodedPassword")
                .build();
    }

    // ========================
    // forgotPassword() tests
    // ========================

    @Test
    @DisplayName("forgotPassword() - valid email returns reset token")
    void forgotPassword_withValidEmail_returnsResetToken() {
        ForgotPasswordRequestDTO request = ForgotPasswordRequestDTO.builder()
                .email("john@gmail.com")
                .build();

        when(userRepository.findByEmail("john@gmail.com")).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        Map<String, String> result = authService.forgotPassword(request);

        assertNotNull(result);
        assertEquals("Password reset token generated successfully", result.get("message"));
        assertNotNull(result.get("resetToken"));

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        assertNotNull(userCaptor.getValue().getResetToken());
        assertNotNull(userCaptor.getValue().getResetTokenExpiry());
    }

    @Test
    @DisplayName("forgotPassword() - invalid email throws UserNotFoundException")
    void forgotPassword_withInvalidEmail_throwsUserNotFoundException() {
        ForgotPasswordRequestDTO request = ForgotPasswordRequestDTO.builder()
                .email("nonexistent@gmail.com")
                .build();

        when(userRepository.findByEmail("nonexistent@gmail.com")).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> authService.forgotPassword(request)
        );

        assertTrue(exception.getMessage().contains("nonexistent@gmail.com"));
        verify(userRepository, never()).save(any(User.class));
    }

    // ========================
    // resetPassword() tests
    // ========================

    @Test
    @DisplayName("resetPassword() - valid token updates password")
    void resetPassword_withValidToken_updatesPassword() {
        String resetToken = "valid-reset-token";
        testUser.setResetToken(resetToken);
        testUser.setResetTokenExpiry(LocalDateTime.now().plusMinutes(10));

        ResetPasswordRequestDTO request = ResetPasswordRequestDTO.builder()
                .email("john@gmail.com")
                .resetToken(resetToken)
                .newPassword("NewPass@123")
                .confirmPassword("NewPass@123")
                .build();

        when(userRepository.findByEmail("john@gmail.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.encode("NewPass@123")).thenReturn("encodedNewPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        Map<String, String> result = authService.resetPassword(request);

        assertNotNull(result);
        assertEquals("Password reset successfully", result.get("message"));

        verify(passwordEncoder).encode("NewPass@123");
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        assertEquals("encodedNewPassword", userCaptor.getValue().getPassword());
    }

    @Test
    @DisplayName("resetPassword() - invalid token throws InvalidResetTokenException")
    void resetPassword_withInvalidToken_throwsInvalidResetTokenException() {
        testUser.setResetToken("correct-token");
        testUser.setResetTokenExpiry(LocalDateTime.now().plusMinutes(10));

        ResetPasswordRequestDTO request = ResetPasswordRequestDTO.builder()
                .email("john@gmail.com")
                .resetToken("wrong-token")
                .newPassword("NewPass@123")
                .confirmPassword("NewPass@123")
                .build();

        when(userRepository.findByEmail("john@gmail.com")).thenReturn(Optional.of(testUser));

        InvalidResetTokenException exception = assertThrows(
                InvalidResetTokenException.class,
                () -> authService.resetPassword(request)
        );

        assertEquals("Invalid reset token", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("resetPassword() - expired token throws InvalidResetTokenException")
    void resetPassword_withExpiredToken_throwsInvalidResetTokenException() {
        String resetToken = "valid-reset-token";
        testUser.setResetToken(resetToken);
        testUser.setResetTokenExpiry(LocalDateTime.now().minusMinutes(5));

        ResetPasswordRequestDTO request = ResetPasswordRequestDTO.builder()
                .email("john@gmail.com")
                .resetToken(resetToken)
                .newPassword("NewPass@123")
                .confirmPassword("NewPass@123")
                .build();

        when(userRepository.findByEmail("john@gmail.com")).thenReturn(Optional.of(testUser));

        InvalidResetTokenException exception = assertThrows(
                InvalidResetTokenException.class,
                () -> authService.resetPassword(request)
        );

        assertEquals("Reset token has expired", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("resetPassword() - mismatched passwords throws InvalidCredentialsException")
    void resetPassword_withMismatchedPasswords_throwsInvalidCredentialsException() {
        String resetToken = "valid-reset-token";
        testUser.setResetToken(resetToken);
        testUser.setResetTokenExpiry(LocalDateTime.now().plusMinutes(10));

        ResetPasswordRequestDTO request = ResetPasswordRequestDTO.builder()
                .email("john@gmail.com")
                .resetToken(resetToken)
                .newPassword("NewPass@123")
                .confirmPassword("DifferentPass@123")
                .build();

        when(userRepository.findByEmail("john@gmail.com")).thenReturn(Optional.of(testUser));

        InvalidCredentialsException exception = assertThrows(
                InvalidCredentialsException.class,
                () -> authService.resetPassword(request)
        );

        assertEquals("Passwords do not match", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("resetPassword() - clears reset token and expiry after success")
    void resetPassword_clearsResetTokenAfterSuccess() {
        String resetToken = "valid-reset-token";
        testUser.setResetToken(resetToken);
        testUser.setResetTokenExpiry(LocalDateTime.now().plusMinutes(10));

        ResetPasswordRequestDTO request = ResetPasswordRequestDTO.builder()
                .email("john@gmail.com")
                .resetToken(resetToken)
                .newPassword("NewPass@123")
                .confirmPassword("NewPass@123")
                .build();

        when(userRepository.findByEmail("john@gmail.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.encode("NewPass@123")).thenReturn("encodedNewPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        authService.resetPassword(request);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();
        assertNull(savedUser.getResetToken());
        assertNull(savedUser.getResetTokenExpiry());
    }
}
