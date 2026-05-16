package com.instagram.auth.controller;

import com.instagram.auth.dto.*;
import com.instagram.auth.service.AuthService;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Page;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "User registration, login, profile management")
public class AuthController {

    private final AuthService authService;
    private final CircuitBreakerRegistry circuitBreakerRegistry;

    @Operation(summary = "Register a new user", description = "Creates a new user account with the provided registration details")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "User registered successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid registration data"),
        @ApiResponse(responseCode = "409", description = "Username or email already exists")
    })
    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> register(@Valid @RequestBody RegisterRequestDTO request) {
        log.info("Registration request received for username: {}", request.getUsername());
        UserResponseDTO response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "User login", description = "Authenticates user credentials and returns a JWT token")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Login successful"),
        @ApiResponse(responseCode = "400", description = "Invalid login request"),
        @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        log.info("Login request received for username: {}", request.getUsername());
        LoginResponseDTO response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get circuit breaker status", description = "Returns the current state of the login circuit breaker")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Circuit breaker status retrieved successfully")
    })
    @GetMapping("/login/status")
    public ResponseEntity<CircuitBreakerStatusDTO> getLoginCircuitBreakerStatus() {
        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker("loginService");
        CircuitBreaker.State state = circuitBreaker.getState();

        long remainingSeconds = 0;
        String message;

        if (state == CircuitBreaker.State.OPEN) {
            long waitDurationMs = circuitBreaker.getCircuitBreakerConfig()
                    .getWaitIntervalFunctionInOpenState().apply(1);
            remainingSeconds = waitDurationMs / 1000;
            message = "Circuit breaker is OPEN. Too many failed login attempts. Please try again after " + remainingSeconds + " seconds.";
        } else if (state == CircuitBreaker.State.HALF_OPEN) {
            message = "Circuit breaker is HALF_OPEN. Testing if service has recovered.";
        } else {
            message = "Circuit breaker is CLOSED. Login service is operating normally.";
        }

        CircuitBreakerStatusDTO status = CircuitBreakerStatusDTO.builder()
                .state(state.name())
                .remainingSeconds(remainingSeconds)
                .message(message)
                .build();

        return ResponseEntity.ok(status);
    }

    @Operation(summary = "Request password reset", description = "Sends a password reset token to the user's email address")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Password reset email sent"),
        @ApiResponse(responseCode = "404", description = "User not found with the provided email")
    })
    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequestDTO request) {
        log.info("Forgot password request for email: {}", request.getEmail());
        Map<String, String> response = authService.forgotPassword(request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Reset password with token", description = "Resets the user's password using a valid reset token")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Password reset successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid or expired reset token")
    })
    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(
            @Valid @RequestBody ResetPasswordRequestDTO request) {
        log.info("Reset password request for email: {}", request.getEmail());
        Map<String, String> response = authService.resetPassword(request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get user profile", description = "Retrieves the profile information for a specific user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Profile retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/profile/{userId}")
    public ResponseEntity<UserProfileResponseDTO> getUserProfile(@PathVariable String userId) {
        log.info("Get profile request for user id: {}", userId);
        UserProfileResponseDTO response = authService.getUserProfile(userId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Update user profile", description = "Updates the profile information for a specific user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Profile updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid profile data"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PutMapping("/profile/{userId}")
    public ResponseEntity<UserProfileResponseDTO> updateProfile(
            @PathVariable String userId,
            @Valid @RequestBody UpdateProfileRequestDTO request) {
        log.info("Update profile request for user id: {}", userId);
        UserProfileResponseDTO response = authService.updateProfile(userId, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Search users", description = "Search for users by username or display name")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Search results returned successfully")
    })
    @GetMapping("/search/users")
    public ResponseEntity<Page<UserResponseDTO>> searchUsers(
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Search users request: q={}", q);
        Page<UserResponseDTO> results = authService.searchUsers(q, page, size);
        return ResponseEntity.ok(results);
    }
}
