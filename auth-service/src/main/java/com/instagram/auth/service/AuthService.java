package com.instagram.auth.service;

import com.instagram.auth.dto.RegisterRequestDTO;
import com.instagram.auth.dto.UserResponseDTO;
import com.instagram.auth.entity.User;
import com.instagram.auth.exception.EmailAlreadyExistsException;
import com.instagram.auth.exception.UsernameAlreadyExistsException;
import com.instagram.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    public UserResponseDTO register(RegisterRequestDTO request) {
        log.info("Registering user with username: {}", request.getUsername());

        // Check username uniqueness
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UsernameAlreadyExistsException(
                    "Username '" + request.getUsername() + "' is already taken");
        }

        // Check email uniqueness
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException(
                    "Email '" + request.getEmail() + "' is already registered");
        }

        // Build user entity
        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        // Save to MongoDB
        User savedUser = userRepository.save(user);
        log.info("User registered successfully with id: {}", savedUser.getId());

        // Convert to response DTO (excludes password)
        return modelMapper.map(savedUser, UserResponseDTO.class);
    }
}
