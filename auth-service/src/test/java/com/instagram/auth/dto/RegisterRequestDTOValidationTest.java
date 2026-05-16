package com.instagram.auth.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class RegisterRequestDTOValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private RegisterRequestDTO buildValidDTO() {
        return RegisterRequestDTO.builder()
                .fullName("John Doe")
                .email("john@gmail.com")
                .username("johndoe")
                .password("Pass@123")
                .confirmPassword("Pass@123")
                .build();
    }

    @Test
    @DisplayName("Valid DTO has no violations")
    void validate_validDTO_noViolations() {
        RegisterRequestDTO dto = buildValidDTO();
        Set<ConstraintViolation<RegisterRequestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "Expected no violations but got: " + violations);
    }

    @Test
    @DisplayName("Username with uppercase letters causes violation")
    void validate_usernameWithUppercase_hasViolation() {
        RegisterRequestDTO dto = buildValidDTO();
        dto.setUsername("JohnDoe");

        Set<ConstraintViolation<RegisterRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v ->
                v.getPropertyPath().toString().equals("username")));
    }

    @Test
    @DisplayName("Password too short (< 8 chars) causes violation")
    void validate_passwordTooShort_hasViolation() {
        RegisterRequestDTO dto = buildValidDTO();
        dto.setPassword("Pa@1");
        dto.setConfirmPassword("Pa@1");

        Set<ConstraintViolation<RegisterRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v ->
                v.getPropertyPath().toString().equals("password")));
    }

    @Test
    @DisplayName("Password too long (> 16 chars) causes violation")
    void validate_passwordTooLong_hasViolation() {
        RegisterRequestDTO dto = buildValidDTO();
        dto.setPassword("Pass@12345678901234");
        dto.setConfirmPassword("Pass@12345678901234");

        Set<ConstraintViolation<RegisterRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v ->
                v.getPropertyPath().toString().equals("password")));
    }

    @Test
    @DisplayName("Password without special character causes violation")
    void validate_passwordWithoutSpecialChar_hasViolation() {
        RegisterRequestDTO dto = buildValidDTO();
        dto.setPassword("Password1");
        dto.setConfirmPassword("Password1");

        Set<ConstraintViolation<RegisterRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v ->
                v.getPropertyPath().toString().equals("password")));
    }

    @Test
    @DisplayName("Email with invalid domain causes violation")
    void validate_emailWithInvalidDomain_hasViolation() {
        RegisterRequestDTO dto = buildValidDTO();
        dto.setEmail("user@domain.xyz");

        Set<ConstraintViolation<RegisterRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v ->
                v.getPropertyPath().toString().equals("email")));
    }

    @Test
    @DisplayName("Mismatched passwords cause violation")
    void validate_mismatchedPasswords_hasViolation() {
        RegisterRequestDTO dto = buildValidDTO();
        dto.setConfirmPassword("Different@1");

        Set<ConstraintViolation<RegisterRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v ->
                v.getPropertyPath().toString().equals("confirmPassword")));
    }

    @Test
    @DisplayName("Blank fullName causes violation")
    void validate_blankFullName_hasViolation() {
        RegisterRequestDTO dto = buildValidDTO();
        dto.setFullName("");

        Set<ConstraintViolation<RegisterRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v ->
                v.getPropertyPath().toString().equals("fullName")));
    }

    @Test
    @DisplayName("Password without uppercase causes violation")
    void validate_passwordWithoutUppercase_hasViolation() {
        RegisterRequestDTO dto = buildValidDTO();
        dto.setPassword("pass@123");
        dto.setConfirmPassword("pass@123");

        Set<ConstraintViolation<RegisterRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v ->
                v.getPropertyPath().toString().equals("password")));
    }

    @Test
    @DisplayName("Password without digit causes violation")
    void validate_passwordWithoutDigit_hasViolation() {
        RegisterRequestDTO dto = buildValidDTO();
        dto.setPassword("Pass@abc");
        dto.setConfirmPassword("Pass@abc");

        Set<ConstraintViolation<RegisterRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v ->
                v.getPropertyPath().toString().equals("password")));
    }
}
