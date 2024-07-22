package com.tinqinacademy.hotel.rest.errorhandling;

import com.tinqinacademy.hotel.api.operations.exception.ErrorWrapper;
import com.tinqinacademy.hotel.api.operations.exception.NotAvailableException;
import com.tinqinacademy.hotel.api.operations.exception.NotFoundException;
import org.postgresql.util.PSQLException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    private final ErrorHandler errorHandler;

    @Autowired
    public GlobalExceptionHandler(ErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }

    @ExceptionHandler
    public ResponseEntity<ErrorWrapper> handleException(MethodArgumentNotValidException ex) {
        ErrorWrapper error = errorHandler.handle(ex, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorWrapper> handleException(NotFoundException ex) {
        ErrorWrapper error = errorHandler.handle(ex, HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorWrapper> handleException(PSQLException ex) {
        ErrorWrapper error = errorHandler.handle(ex, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorWrapper> handleException(NotAvailableException ex) {
        ErrorWrapper error = errorHandler.handle(ex, HttpStatus.CONFLICT);
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }
}
