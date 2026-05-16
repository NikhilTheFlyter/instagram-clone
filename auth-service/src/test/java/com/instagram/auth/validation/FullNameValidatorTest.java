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
class FullNameValidatorTest {

    private FullNameValidator validator;

    @Mock
    private ConstraintValidatorContext context;

    @BeforeEach
    void setUp() {
        validator = new FullNameValidator();
    }

    @Test
    @DisplayName("'John Doe' is valid - two properly capitalized words")
    void isValid_johnDoe_returnsTrue() {
        assertTrue(validator.isValid("John Doe", context));
    }

    @Test
    @DisplayName("'John' is valid - single capitalized word")
    void isValid_singleWord_returnsTrue() {
        assertTrue(validator.isValid("John", context));
    }

    @Test
    @DisplayName("'john doe' is invalid - not capitalized")
    void isValid_allLowercase_returnsFalse() {
        assertFalse(validator.isValid("john doe", context));
    }

    @Test
    @DisplayName("'John doe' is invalid - second word not capitalized")
    void isValid_secondWordNotCapitalized_returnsFalse() {
        assertFalse(validator.isValid("John doe", context));
    }

    @Test
    @DisplayName("'John123' is invalid - contains digits")
    void isValid_containsDigits_returnsFalse() {
        assertFalse(validator.isValid("John123", context));
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
    @DisplayName("'John Paul Smith' is valid - three properly capitalized words")
    void isValid_threeWords_returnsTrue() {
        assertTrue(validator.isValid("John Paul Smith", context));
    }

    @Test
    @DisplayName("'John@Doe' is invalid - contains special characters")
    void isValid_containsSpecialChars_returnsFalse() {
        assertFalse(validator.isValid("John@Doe", context));
    }
}
