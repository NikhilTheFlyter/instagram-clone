package com.instagram.auth.service;

import com.instagram.auth.dto.*;
import com.instagram.auth.entity.User;
import com.instagram.auth.exception.*;
import com.instagram.auth.repository.UserRepository;
import com.instagram.auth.util.JwtUtil;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final JwtUtil jwtUtil;

    public UserResponseDTO register(RegisterRequestDTO request) {
        log.info("Registering user with username: {}", request.getUsername());

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UsernameAlreadyExistsException(
                    "Username '" + request.getUsername() + "' is already taken");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException(
                    "Email '" + request.getEmail() + "' is already registered");
        }

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        User savedUser = userRepository.save(user);
        log.info("User registered successfully with id: {}", savedUser.getId());

        return modelMapper.map(savedUser, UserResponseDTO.class);
    }

    @CircuitBreaker(name = "loginService", fallbackMethod = "loginFallback")
    public LoginResponseDTO login(LoginRequestDTO request) {
        log.info("Login attempt for username: {}", request.getUsername());

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid username or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid username or password");
        }

        String token = jwtUtil.generateToken(user.getUsername(), user.getId());
        log.info("User logged in successfully: {}", user.getUsername());

        return LoginResponseDTO.builder()
                .token(token)
                .username(user.getUsername())
                .message("Login successful")
                .build();
    }

    public LoginResponseDTO loginFallback(LoginRequestDTO request, Throwable throwable) {
        log.warn("Circuit breaker is open. Login attempt blocked for username: {}", request.getUsername());
        return LoginResponseDTO.builder()
                .token(null)
                .username(request.getUsername())
                .message("Too many failed login attempts. Service is temporarily unavailable. Please try again after 60 seconds.")
                .build();
    }

    public UserProfileResponseDTO getUserProfile(String userId) {
        log.info("Fetching profile for user id: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        return UserProfileResponseDTO.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .username(user.getUsername())
                .bio(user.getBio())
                .profilePicture(user.getProfilePicture())
                .createdAt(user.getCreatedAt())
                .postCount(0)
                .followerCount(0)
                .followingCount(0)
                .build();
    }

    public UserProfileResponseDTO updateProfile(String userId, UpdateProfileRequestDTO dto) {
        log.info("Updating profile for user id: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        if (dto.getFullName() != null) {
            user.setFullName(dto.getFullName());
        }
        if (dto.getBio() != null) {
            user.setBio(dto.getBio());
        }
        if (dto.getProfilePicture() != null) {
            user.setProfilePicture(dto.getProfilePicture());
        }

        User updatedUser = userRepository.save(user);
        log.info("Profile updated successfully for user id: {}", userId);

        return UserProfileResponseDTO.builder()
                .id(updatedUser.getId())
                .fullName(updatedUser.getFullName())
                .email(updatedUser.getEmail())
                .username(updatedUser.getUsername())
                .bio(updatedUser.getBio())
                .profilePicture(updatedUser.getProfilePicture())
                .createdAt(updatedUser.getCreatedAt())
                .postCount(0)
                .followerCount(0)
                .followingCount(0)
                .build();
    }

    public Map<String, String> forgotPassword(ForgotPasswordRequestDTO request) {
        log.info("Forgot password request for email: {}", request.getEmail());

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserNotFoundException(
                        "No account found with email: " + request.getEmail()));

        // Generate reset token with 15 minute expiry
        String resetToken = UUID.randomUUID().toString();
        user.setResetToken(resetToken);
        user.setResetTokenExpiry(LocalDateTime.now().plusMinutes(15));
        userRepository.save(user);

        log.info("Reset token generated for user: {}", user.getUsername());

        return Map.of(
                "message", "Password reset token generated successfully",
                "resetToken", resetToken
        );
    }

    public Map<String, String> resetPassword(ResetPasswordRequestDTO request) {
        log.info("Reset password request for email: {}", request.getEmail());

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserNotFoundException(
                        "No account found with email: " + request.getEmail()));

        // Validate reset token
        if (user.getResetToken() == null || !user.getResetToken().equals(request.getResetToken())) {
            throw new InvalidResetTokenException("Invalid reset token");
        }

        // Check token expiry
        if (user.getResetTokenExpiry() == null || LocalDateTime.now().isAfter(user.getResetTokenExpiry())) {
            throw new InvalidResetTokenException("Reset token has expired");
        }

        // Validate password match
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new InvalidCredentialsException("Passwords do not match");
        }

        // Update password and clear reset token
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        userRepository.save(user);

        log.info("Password reset successfully for user: {}", user.getUsername());

        return Map.of("message", "Password reset successfully");
    }

    public Page<UserResponseDTO> searchUsers(String query, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> users = userRepository.searchUsers(query, pageable);
        return users.map(user -> modelMapper.map(user, UserResponseDTO.class));
    }
}
