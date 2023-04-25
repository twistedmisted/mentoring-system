package ua.kpi.mishchenko.mentoringsystem.validation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ua.kpi.mishchenko.mentoringsystem.validation.UniqueSetElements;

import java.util.HashSet;
import java.util.Set;

public class UniqueSetElementsValidator implements ConstraintValidator<UniqueSetElements, Set<String>> {

    @Override
    public boolean isValid(Set<String> strings, ConstraintValidatorContext constraintValidatorContext) {
        Set<String> uniqueElements = new HashSet<>();
        for (String string : strings) {
            if (uniqueElements.contains(string.toLowerCase())) {
                return false;
            }
            uniqueElements.add(string.toLowerCase());
        }
        return true;
    }
}
