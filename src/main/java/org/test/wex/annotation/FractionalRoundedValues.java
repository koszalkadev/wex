package org.test.wex.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = FractionRoundedValuesValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface FractionalRoundedValues {
    String message() default "Number is not correctly rounded";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    int fractionDigits();
}