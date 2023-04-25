package ua.kpi.mishchenko.mentoringsystem.validation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ua.kpi.mishchenko.mentoringsystem.validation.SetSize;

import java.util.Set;

public class SetSizeValidator implements ConstraintValidator<SetSize, Set<String>> {

    private int minSize;
    private int maxSize;

    @Override
    public void initialize(SetSize constraintAnnotation) {
        minSize = constraintAnnotation.min();
        maxSize = constraintAnnotation.max();
    }

    @Override
    public boolean isValid(Set<String> values, ConstraintValidatorContext constraintValidatorContext) {
        return values.size() >= minSize && values.size() <= maxSize;
    }
}
