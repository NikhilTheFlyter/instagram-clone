package com.instagram.auth.controller;

import com.instagram.auth.dto.*;
import com.instagram.auth.service.AuthService;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;
    private final CircuitBreakerRegistry circuitBreakerRegistry;

    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> register(@Valid @RequestBody RegisterRequestDTO request) {
        log.info("Registration request received for username: {}", request.getUsername());
        UserResponseDTO response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        log.info("Login request received for username: {}", request.getUsername());
        LoginResponseDTO response = authService.login(request);
        return ResponseEntity.ok(response);
    }

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

    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequestDTO request) {
        log.info("Forgot password request for email: {}", request.getEmail());
        Map<String, String> response = authService.forgotPassword(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(
            @Valid @RequestBody ResetPasswordRequestDTO request) {
        log.info("Reset password request for email: {}", request.getEmail());
        Map<String, String> response = authService.resetPassword(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/profile/{userId}")
    public ResponseEntity<UserProfileResponseDTO> getUserProfile(@PathVariable String userId) {
        log.info("Get profile request for user id: {}", userId);
        UserProfileResponseDTO response = authService.getUserProfile(userId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/profile/{userId}")
    public ResponseEntity<UserProfileResponseDTO> updateProfile(
            @PathVariable String userId,
            @Valid @RequestBody UpdateProfileRequestDTO request) {
        log.info("Update profile request for user id: {}", userId);
        UserProfileResponseDTO response = authService.updateProfile(userId, request);
        return ResponseEntity.ok(response);
    }
}
