package ru.practicum.explore.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

public class EventDateValidator implements ConstraintValidator<StartDateValidation, LocalDateTime> {
    @Override
    public void initialize(StartDateValidation constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(LocalDateTime dateTime, ConstraintValidatorContext context) {
        if (dateTime == null) {
            return true;
        } else return dateTime.isAfter(LocalDateTime.now().plusHours(2));
    }
}
