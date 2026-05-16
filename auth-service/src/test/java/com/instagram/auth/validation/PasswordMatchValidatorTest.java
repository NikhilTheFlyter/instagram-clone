package com.instagram.auth.validation;

import com.instagram.auth.dto.RegisterRequestDTO;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PasswordMatchValidatorTest {

    private PasswordMatchValidator validator;

    @Mock
    private ConstraintValidatorContext context;

    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder violationBuilder;

    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext nodeBuilder;

    @BeforeEach
    void setUp() {
        validator = new PasswordMatchValidator();
    }

    @Test
    @DisplayName("Matching passwords are valid")
    void isValid_matchingPasswords_returnsTrue() {
        RegisterRequestDTO dto = RegisterRequestDTO.builder()
                .password("Pass@123")
                .confirmPassword("Pass@123")
                .build();

        assertTrue(validator.isValid(dto, context));
    }

    @Test
    @DisplayName("Non-matching passwords are invalid")
    void isValid_nonMatchingPasswords_returnsFalse() {
        RegisterRequestDTO dto = RegisterRequestDTO.builder()
                .password("Pass@123")
                .confirmPassword("Different@1")
                .build();

        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(violationBuilder);
        when(violationBuilder.addPropertyNode(anyString())).thenReturn(nodeBuilder);
        when(nodeBuilder.addConstraintViolation()).thenReturn(context);

        assertFalse(validator.isValid(dto, context));

        verify(context).disableDefaultConstraintViolation();
        verify(context).buildConstraintViolationWithTemplate("Passwords do not match");
    }

    @Test
    @DisplayName("Null password is invalid")
    void isValid_nullPassword_returnsFalse() {
        RegisterRequestDTO dto = RegisterRequestDTO.builder()
                .password(null)
                .confirmPassword("Pass@123")
                .build();

        assertFalse(validator.isValid(dto, context));
    }

    @Test
    @DisplayName("Null confirmPassword is invalid")
    void isValid_nullConfirmPassword_returnsFalse() {
        RegisterRequestDTO dto = RegisterRequestDTO.builder()
                .password("Pass@123")
                .confirmPassword(null)
                .build();

        assertFalse(validator.isValid(dto, context));
    }

    @Test
    @DisplayName("Both null passwords are invalid")
    void isValid_bothNull_returnsFalse() {
        RegisterRequestDTO dto = RegisterRequestDTO.builder()
                .password(null)
                .confirmPassword(null)
                .build();

        assertFalse(validator.isValid(dto, context));
    }
}
