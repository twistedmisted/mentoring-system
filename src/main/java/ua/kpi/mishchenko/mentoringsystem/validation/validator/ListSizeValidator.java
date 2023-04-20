package ua.kpi.mishchenko.mentoringsystem.validation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ua.kpi.mishchenko.mentoringsystem.validation.ListSize;

import java.util.List;

public class ListSizeValidator implements ConstraintValidator<ListSize, List<String>> {

    private int minSize;
    private int maxSize;

    @Override
    public void initialize(ListSize constraintAnnotation) {
        minSize = constraintAnnotation.min();
        maxSize = constraintAnnotation.max();
    }

    @Override
    public boolean isValid(List<String> values, ConstraintValidatorContext constraintValidatorContext) {
        return values.size() >= minSize && values.size() <= maxSize;
    }
}
