package com.tinqinacademy.hotel.rest.errorhandling;

import com.tinqinacademy.hotel.api.operations.exception.ErrorWrapper;
import com.tinqinacademy.hotel.api.operations.exception.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;

public interface ErrorHandler {
    ErrorWrapper handle(MethodArgumentNotValidException ex, HttpStatus status);
    ErrorWrapper handle(NotFoundException ex, HttpStatus status);
}
