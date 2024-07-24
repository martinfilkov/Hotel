package com.tinqinacademy.hotel.rest.errorhandling;

import com.tinqinacademy.hotel.api.operations.exception.*;
import org.postgresql.util.PSQLException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class ErrorHandlerImpl implements ErrorHandler {
    @Override
    public ErrorWrapper handle(MethodArgumentNotValidException ex, HttpStatus status) {
        List<ErrorResponse> errors = new ArrayList<>();

        ex.getBindingResult().getFieldErrors().forEach(e ->
                errors.add(buildErrorResponse(status, e.getDefaultMessage())));

        ErrorWrapper error = buildErrorWrapper(errors);

        return error;
    }

    @Override
    public ErrorWrapper handle(CustomException ex, HttpStatus status){
        ErrorResponse errorResponse = buildErrorResponse(status, ex.getMessage());

        ErrorWrapper error = buildErrorWrapper(List.of(errorResponse));

        return error;
    }

    @Override
    public ErrorWrapper handle(PSQLException ex, HttpStatus status){
        ErrorResponse errorResponse = buildErrorResponse(status, ex.getMessage());

        ErrorWrapper error = buildErrorWrapper(List.of(errorResponse));

        return error;
    }

    @Override
    public ErrorWrapper handle(Exception ex, HttpStatus status) {
        ErrorResponse errorResponse = buildErrorResponse(status, ex.getMessage());

        ErrorWrapper error = buildErrorWrapper(List.of(errorResponse));

        return error;
    }

    private ErrorResponse buildErrorResponse(HttpStatus status, String message){
        return ErrorResponse.builder()
                .status(status.value())
                .message(message)
                .build();
    }

    private ErrorWrapper buildErrorWrapper(List<ErrorResponse> errors){
        return ErrorWrapper.builder()
                .errors(errors)
                .timestamp(new Date(System.currentTimeMillis()))
                .build();
    }
}
