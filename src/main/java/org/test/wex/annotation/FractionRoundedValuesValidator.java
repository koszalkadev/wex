package org.test.wex.annotation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.math.BigDecimal;

public class FractionRoundedValuesValidator implements ConstraintValidator<FractionalRoundedValues, BigDecimal> {
    private int fractionDigits;

    @Override
    public void initialize(FractionalRoundedValues constraintAnnotation) {
        this.fractionDigits = constraintAnnotation.fractionDigits();
    }

    @Override
    public boolean isValid(BigDecimal value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        String[] parts = String.valueOf(value.doubleValue()).split("\\.");
        return parts[1].length() <= this.fractionDigits;
    }
}
