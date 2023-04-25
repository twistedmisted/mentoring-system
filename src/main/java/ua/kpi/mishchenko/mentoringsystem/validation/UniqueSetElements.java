package ua.kpi.mishchenko.mentoringsystem.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import ua.kpi.mishchenko.mentoringsystem.validation.validator.UniqueSetElementsValidator;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Constraint(validatedBy = UniqueSetElementsValidator.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueSetElements {

    String message() default "The set contains not unique elements.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
