package com.instagram.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.instagram.auth.dto.RegisterRequestDTO;
import com.instagram.auth.dto.UserResponseDTO;
import com.instagram.auth.exception.EmailAlreadyExistsException;
import com.instagram.auth.exception.UsernameAlreadyExistsException;
import com.instagram.auth.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = AuthController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    private RegisterRequestDTO validRequest;
    private UserResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        validRequest = RegisterRequestDTO.builder()
                .fullName("John Doe")
                .email("john@gmail.com")
                .username("johndoe")
                .password("Pass@123")
                .confirmPassword("Pass@123")
                .build();

        responseDTO = UserResponseDTO.builder()
                .id("abc123")
                .fullName("John Doe")
                .email("john@gmail.com")
                .username("johndoe")
                .createdAt(LocalDateTime.of(2024, 1, 1, 0, 0))
                .build();
    }

    @Test
    @DisplayName("POST /api/auth/register - valid data returns 201 with response body")
    void register_withValidData_returns201() throws Exception {
        when(authService.register(any(RegisterRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("abc123"))
                .andExpect(jsonPath("$.fullName").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john@gmail.com"))
                .andExpect(jsonPath("$.username").value("johndoe"));
    }

    @Test
    @DisplayName("POST /api/auth/register - duplicate username returns 409")
    void register_withDuplicateUsername_returns409() throws Exception {
        when(authService.register(any(RegisterRequestDTO.class)))
                .thenThrow(new UsernameAlreadyExistsException("Username 'johndoe' is already taken"));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Username 'johndoe' is already taken"))
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    @DisplayName("POST /api/auth/register - duplicate email returns 409")
    void register_withDuplicateEmail_returns409() throws Exception {
        when(authService.register(any(RegisterRequestDTO.class)))
                .thenThrow(new EmailAlreadyExistsException("Email 'john@gmail.com' is already registered"));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Email 'john@gmail.com' is already registered"))
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    @DisplayName("POST /api/auth/register - empty fields returns 400 with validation errors")
    void register_withEmptyFields_returns400() throws Exception {
        RegisterRequestDTO emptyRequest = RegisterRequestDTO.builder()
                .fullName("")
                .email("")
                .username("")
                .password("")
                .confirmPassword("")
                .build();

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emptyRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.status").value(400));
    }
}
