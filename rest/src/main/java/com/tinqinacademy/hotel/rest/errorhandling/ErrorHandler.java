package com.tinqinacademy.hotel.rest.errorhandling;

import com.tinqinacademy.hotel.api.operations.exception.CustomException;
import com.tinqinacademy.hotel.api.operations.exception.ErrorWrapper;
import org.postgresql.util.PSQLException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;

public interface ErrorHandler {
    ErrorWrapper handle(MethodArgumentNotValidException ex, HttpStatus status);

    ErrorWrapper handle(CustomException ex, HttpStatus status);

    ErrorWrapper handle(PSQLException ex, HttpStatus status);
}
