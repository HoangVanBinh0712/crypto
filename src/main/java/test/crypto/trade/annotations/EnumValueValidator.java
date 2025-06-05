package test.crypto.trade.annotations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class EnumValueValidator implements ConstraintValidator<EnumValue, String> {

    private Enum<?>[] enumConstants;

    @Override
    public void initialize(EnumValue constraintAnnotation) {
        this.enumConstants = constraintAnnotation.enumClass().getEnumConstants();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return true;

        for (Enum<?> e : enumConstants) {
            if (e.name().equalsIgnoreCase(value)) {
                return true;
            }
        }
        return false;
    }
}