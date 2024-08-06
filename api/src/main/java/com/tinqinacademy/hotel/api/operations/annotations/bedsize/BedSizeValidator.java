package com.tinqinacademy.hotel.api.operations.annotations.bedsize;

import com.tinqinacademy.hotel.api.operations.model.BedSize;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class BedSizeValidator implements ConstraintValidator<BedSizeValidation, List<String>> {
    private static final Set<String> VALID_BED_SIZES =
            EnumSet.allOf(BedSize.class).stream()
                    .filter(bedSize -> bedSize != BedSize.UNKNOWN)
                    .map(BedSize::toString)
                    .collect(Collectors.toSet());

    private boolean optional;

    @Override
    public void initialize(BedSizeValidation constraintAnnotation) {
        this.optional = constraintAnnotation.optional();
    }

    @Override
    public boolean isValid(List<String> value, ConstraintValidatorContext context) {
        if (optional && (value == null || value.isEmpty())) {
            return true;
        }

        if (value == null || value.isEmpty()) {
            return false;
        }

        for (String bedSize : value) {
            if (!VALID_BED_SIZES.contains(bedSize)) {
                return false;
            }
        }
        return true;
    }
}
