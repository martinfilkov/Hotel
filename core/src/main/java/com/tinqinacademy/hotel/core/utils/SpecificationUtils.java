package com.tinqinacademy.hotel.core.utils;

import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Objects;

public class SpecificationUtils {
    public static <T> Specification<T> combineSpecifications(List<Specification<T>> specifications) {
        List<Specification<T>> nonNullSpecifications = specifications.stream()
            .filter(Objects::nonNull)
            .toList();

        Specification<T> combinedSpec = nonNullSpecifications.getFirst();
        for (int i = 1; i < nonNullSpecifications.size(); i++) {
            combinedSpec = combinedSpec.and(nonNullSpecifications.get(i));
        }
        return combinedSpec;
    }
}
