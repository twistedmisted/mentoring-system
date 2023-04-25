package ua.kpi.mishchenko.mentoringsystem.validation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ua.kpi.mishchenko.mentoringsystem.validation.UniqueSetElements;

import java.util.Set;

public class UniqueSetElementsValidator implements ConstraintValidator<UniqueSetElements, Set<String>> {

    @Override
    public boolean isValid(Set<String> strings, ConstraintValidatorContext constraintValidatorContext) {
//        for (String string : strings) {
//            string.con
//        }
        return true;
    }
}
