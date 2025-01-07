package com.studia.JavaWebApplication.validate;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = UniqueImageValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueImage {
    String message() default "Ten sam obraz jest już przypisany do innego produktu.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}