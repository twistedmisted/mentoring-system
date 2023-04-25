package ua.kpi.mishchenko.mentoringsystem.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import ua.kpi.mishchenko.mentoringsystem.validation.validator.SetSizeValidator;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Constraint(validatedBy = SetSizeValidator.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface SetSize {

    String message() default "The size does not meet the conditions";

    int min() default 0;

    int max() default 0;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
