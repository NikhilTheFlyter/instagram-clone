package com.instagram.auth.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class FullNameValidator implements ConstraintValidator<ValidFullName, String> {

    @Override
    public boolean isValid(String fullName, ConstraintValidatorContext context) {
        if (fullName == null || fullName.isBlank()) {
            return false;
        }

        // Must contain only English letters and spaces
        if (!fullName.matches("^[a-zA-Z\\s]+$")) {
            return false;
        }

        // Split into words, check each word starts with uppercase
        String[] words = fullName.trim().split("\\s+");
        if (words.length < 1) {
            return false;
        }

        for (String word : words) {
            if (word.isEmpty() || !Character.isUpperCase(word.charAt(0))) {
                return false;
            }
        }

        return true;
    }
}
