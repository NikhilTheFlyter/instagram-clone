package com.instagram.auth.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = FullNameValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidFullName {

    String message() default "Full name must contain only English letters, minimum 1 word, and each word must start with a capital letter";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
