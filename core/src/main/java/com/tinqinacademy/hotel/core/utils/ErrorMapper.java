package com.tinqinacademy.hotel.core.utils;

import com.tinqinacademy.hotel.api.operations.base.OperationInput;
import com.tinqinacademy.hotel.api.operations.exception.ErrorResponse;
import com.tinqinacademy.hotel.api.operations.exception.ErrorWrapper;
import jakarta.validation.ConstraintViolation;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Component
public class ErrorMapper {
    public ErrorWrapper handleError(Throwable ex, HttpStatusCode statusCode) {
        return ErrorWrapper
                .builder()
                .errors(List.of(ErrorResponse.builder()
                        .message(ex.getMessage())
                        .build()))
                .code(statusCode.value())
                .timestamp(Timestamp.valueOf(LocalDateTime.now()))
                .build();
    }

    public ErrorWrapper handleViolations(Set<ConstraintViolation<OperationInput>> violations, HttpStatusCode statusCode) {
        List<ErrorResponse> responses = violations.stream()
                .map(v -> ErrorResponse.builder()
                        .message(v.getMessage())
                        .build())
                .toList();

        return ErrorWrapper.builder()
                .errors(responses)
                .code(statusCode.value())
                .timestamp(Timestamp.valueOf(LocalDateTime.now()))
                .build();
    }
}
