package com.tinqinacademy.hotel.core.services.operation;

import com.tinqinacademy.hotel.api.operations.base.Errors;
import com.tinqinacademy.hotel.api.operations.base.OperationInput;
import com.tinqinacademy.hotel.api.operations.exception.ErrorWrapper;
import com.tinqinacademy.hotel.core.utils.ErrorMapper;
import io.vavr.control.Either;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Set;


@Slf4j
@Service
public class BaseOperationProcessor {
    protected final ConversionService conversionService;
    protected final Validator validator;
    protected final ErrorMapper errorMapper;

    protected BaseOperationProcessor(ConversionService conversionService, Validator validator, ErrorMapper errorMapper) {
        this.conversionService = conversionService;
        this.validator = validator;
        this.errorMapper = errorMapper;
    }

    public <T extends OperationInput> Either<Errors, T> validateInput(T input) {
        Set<ConstraintViolation<OperationInput>> constraintViolations = validator.validate(input);

        if (constraintViolations.isEmpty()) {
            return Either.right(input);
        }

        ErrorWrapper errors = errorMapper.handleViolations(constraintViolations, HttpStatus.BAD_REQUEST);
        return Either.left(errors);
    }
}
