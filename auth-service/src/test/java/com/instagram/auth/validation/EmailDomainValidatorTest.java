package com.instagram.auth.validation;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class EmailDomainValidatorTest {

    private EmailDomainValidator validator;

    @Mock
    private ConstraintValidatorContext context;

    @BeforeEach
    void setUp() {
        validator = new EmailDomainValidator();
    }

    @Test
    @DisplayName("'user@gmail.com' is valid - .com domain")
    void isValid_comDomain_returnsTrue() {
        assertTrue(validator.isValid("user@gmail.com", context));
    }

    @Test
    @DisplayName("'user@company.org' is valid - .org domain")
    void isValid_orgDomain_returnsTrue() {
        assertTrue(validator.isValid("user@company.org", context));
    }

    @Test
    @DisplayName("'user@domain.in' is valid - .in domain")
    void isValid_inDomain_returnsTrue() {
        assertTrue(validator.isValid("user@domain.in", context));
    }

    @Test
    @DisplayName("'user@domain.xyz' is invalid - .xyz not in allowed domains")
    void isValid_xyzDomain_returnsFalse() {
        assertFalse(validator.isValid("user@domain.xyz", context));
    }

    @Test
    @DisplayName("'user@domain.net' is invalid - .net not in allowed domains")
    void isValid_netDomain_returnsFalse() {
        assertFalse(validator.isValid("user@domain.net", context));
    }

    @Test
    @DisplayName("'invalid-email' is invalid - no @ sign")
    void isValid_noAtSign_returnsFalse() {
        assertFalse(validator.isValid("invalid-email", context));
    }

    @Test
    @DisplayName("Empty string is invalid")
    void isValid_emptyString_returnsFalse() {
        assertFalse(validator.isValid("", context));
    }

    @Test
    @DisplayName("null is invalid")
    void isValid_null_returnsFalse() {
        assertFalse(validator.isValid(null, context));
    }

    @Test
    @DisplayName("'user@sub.domain.com' is valid - subdomain with .com")
    void isValid_subDomain_returnsTrue() {
        assertTrue(validator.isValid("user@sub.domain.com", context));
    }
}
