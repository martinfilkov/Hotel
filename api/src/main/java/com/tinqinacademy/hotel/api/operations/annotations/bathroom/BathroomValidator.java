package com.tinqinacademy.hotel.api.operations.annotations.bathroom;

import com.tinqinacademy.hotel.api.operations.model.BathroomType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;

public class BathroomValidator implements ConstraintValidator<BathroomValidation, String> {
    private static final Set<String> VALID_BATHROOM_TYPES =
            EnumSet.allOf(BathroomType.class)
                    .stream()
                    .filter(bathroom -> bathroom != BathroomType.UNKNOWN)
                    .map(BathroomType::toString)
                    .collect(Collectors.toSet());

    private boolean optional;

    @Override
    public void initialize(BathroomValidation bathroomValidation) {
        this.optional = bathroomValidation.optional();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (optional && (value == null || value.isEmpty())){
            return true;
        }

        if (value == null || value.isEmpty()){
            return false;
        }

        return VALID_BATHROOM_TYPES.contains(value);
    }
}
