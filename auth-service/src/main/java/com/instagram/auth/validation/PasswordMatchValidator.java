package com.instagram.auth.validation;

import com.instagram.auth.dto.RegisterRequestDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchValidator implements ConstraintValidator<PasswordMatch, RegisterRequestDTO> {

    @Override
    public boolean isValid(RegisterRequestDTO dto, ConstraintValidatorContext context) {
        if (dto.getPassword() == null || dto.getConfirmPassword() == null) {
            return false;
        }

        boolean matches = dto.getPassword().equals(dto.getConfirmPassword());

        if (!matches) {
            // Bind the error to the confirmPassword field
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Passwords do not match")
                    .addPropertyNode("confirmPassword")
                    .addConstraintViolation();
        }

        return matches;
    }
}
