package com.instagram.auth.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Set;

public class EmailDomainValidator implements ConstraintValidator<ValidEmailDomain, String> {

    private static final Set<String> ALLOWED_DOMAINS = Set.of("com", "org", "in");

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        if (email == null || email.isBlank()) {
            return false;
        }

        // Basic email format check
        if (!email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            return false;
        }

        // Extract TLD and check against allowed domains
        String tld = email.substring(email.lastIndexOf('.') + 1).toLowerCase();
        return ALLOWED_DOMAINS.contains(tld);
    }
}
