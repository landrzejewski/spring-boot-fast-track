package pl.training.common.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public final class RangeValidator implements ConstraintValidator<Range, Double> {

    private double minValue;
    private double maxValue;

    @Override
    public void initialize(final Range constraintAnnotation) {
        this.minValue = constraintAnnotation.minValue();
        this.maxValue = constraintAnnotation.maxValue();
    }

    @Override
    public boolean isValid(final Double value, final ConstraintValidatorContext context) {
        return value >= minValue && value <= maxValue;
    }

}
