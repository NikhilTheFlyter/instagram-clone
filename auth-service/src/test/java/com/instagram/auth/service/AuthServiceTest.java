package com.instagram.auth.service;

import com.instagram.auth.dto.RegisterRequestDTO;
import com.instagram.auth.dto.UserResponseDTO;
import com.instagram.auth.entity.User;
import com.instagram.auth.exception.EmailAlreadyExistsException;
import com.instagram.auth.exception.UsernameAlreadyExistsException;
import com.instagram.auth.repository.UserRepository;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private AuthService authService;

    private RegisterRequestDTO validRequest;
    private User savedUser;
    private UserResponseDTO expectedResponse;

    @BeforeEach
    void setUp() {
        validRequest = RegisterRequestDTO.builder()
                .fullName("John Doe")
                .email("john@gmail.com")
                .username("johndoe")
                .password("Pass@123")
                .confirmPassword("Pass@123")
                .build();

        savedUser = User.builder()
                .id("abc123")
                .fullName("John Doe")
                .email("john@gmail.com")
                .username("johndoe")
                .password("encodedPassword")
                .createdAt(LocalDateTime.now())
                .build();

        expectedResponse = UserResponseDTO.builder()
                .id("abc123")
                .fullName("John Doe")
                .email("john@gmail.com")
                .username("johndoe")
                .createdAt(savedUser.getCreatedAt())
                .build();
    }

    @Test
    @DisplayName("register() - happy path returns UserResponseDTO")
    void register_withValidInput_returnsUserResponseDTO() {
        when(userRepository.existsByUsername("johndoe")).thenReturn(false);
        when(userRepository.existsByEmail("john@gmail.com")).thenReturn(false);
        when(passwordEncoder.encode("Pass@123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(modelMapper.map(savedUser, UserResponseDTO.class)).thenReturn(expectedResponse);

        UserResponseDTO result = authService.register(validRequest);

        assertNotNull(result);
        assertEquals("abc123", result.getId());
        assertEquals("John Doe", result.getFullName());
        assertEquals("john@gmail.com", result.getEmail());
        assertEquals("johndoe", result.getUsername());
    }

    @Test
    @DisplayName("register() - duplicate username throws UsernameAlreadyExistsException")
    void register_withDuplicateUsername_throwsUsernameAlreadyExistsException() {
        when(userRepository.existsByUsername("johndoe")).thenReturn(true);

        UsernameAlreadyExistsException exception = assertThrows(
                UsernameAlreadyExistsException.class,
                () -> authService.register(validRequest)
        );

        assertTrue(exception.getMessage().contains("johndoe"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("register() - duplicate email throws EmailAlreadyExistsException")
    void register_withDuplicateEmail_throwsEmailAlreadyExistsException() {
        when(userRepository.existsByUsername("johndoe")).thenReturn(false);
        when(userRepository.existsByEmail("john@gmail.com")).thenReturn(true);

        EmailAlreadyExistsException exception = assertThrows(
                EmailAlreadyExistsException.class,
                () -> authService.register(validRequest)
        );

        assertTrue(exception.getMessage().contains("john@gmail.com"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("register() - password is encoded before saving")
    void register_encodesPasswordBeforeSaving() {
        when(userRepository.existsByUsername("johndoe")).thenReturn(false);
        when(userRepository.existsByEmail("john@gmail.com")).thenReturn(false);
        when(passwordEncoder.encode("Pass@123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(modelMapper.map(savedUser, UserResponseDTO.class)).thenReturn(expectedResponse);

        authService.register(validRequest);

        verify(passwordEncoder).encode("Pass@123");

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        assertEquals("encodedPassword", userCaptor.getValue().getPassword());
    }

    @Test
    @DisplayName("register() - repository.save() is called exactly once")
    void register_callsSaveExactlyOnce() {
        when(userRepository.existsByUsername("johndoe")).thenReturn(false);
        when(userRepository.existsByEmail("john@gmail.com")).thenReturn(false);
        when(passwordEncoder.encode("Pass@123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(modelMapper.map(savedUser, UserResponseDTO.class)).thenReturn(expectedResponse);

        authService.register(validRequest);

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("register() - ModelMapper converts saved User to UserResponseDTO")
    void register_usesModelMapperToConvert() {
        when(userRepository.existsByUsername("johndoe")).thenReturn(false);
        when(userRepository.existsByEmail("john@gmail.com")).thenReturn(false);
        when(passwordEncoder.encode("Pass@123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(modelMapper.map(savedUser, UserResponseDTO.class)).thenReturn(expectedResponse);

        UserResponseDTO result = authService.register(validRequest);

        verify(modelMapper).map(eq(savedUser), eq(UserResponseDTO.class));
        assertSame(expectedResponse, result);
    }
}
