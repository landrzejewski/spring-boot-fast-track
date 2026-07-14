package pl.training.common.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

// @Component
public class RangeValidator implements ConstraintValidator<Range, Double> {

    private double minValue;
    private double maxValue;

    @Override
    public void initialize(Range range) {
        minValue = range.minValue();
        maxValue = range.maxValue();
    }

    @Override
    public boolean isValid(Double value, ConstraintValidatorContext context) {
        return value >= minValue && value <= maxValue;
    }

}
