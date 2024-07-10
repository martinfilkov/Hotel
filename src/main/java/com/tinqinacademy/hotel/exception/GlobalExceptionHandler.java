package com.tinqinacademy.hotel.exception;

import com.tinqinacademy.hotel.utils.ErrorUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    private final ErrorUtil errorUtil;

    @Autowired
    public GlobalExceptionHandler(ErrorUtil errorUtil) {
        this.errorUtil = errorUtil;
    }

    @ExceptionHandler
    public ResponseEntity<ErrorWrapper> handleException(MethodArgumentNotValidException ex) {
        ErrorWrapper error = errorUtil.handle(ex, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorWrapper> handleException(NotFoundException ex) {
        ErrorWrapper error = errorUtil.handle(ex, HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }
}
