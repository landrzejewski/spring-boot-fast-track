package pl.training.common.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = RangeValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Range {

    String message() default "{invalidRange}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    double minValue() default 100;

    double maxValue() default 10_000;

}
