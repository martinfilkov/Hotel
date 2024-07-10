package com.tinqinacademy.hotel.utils;

import com.tinqinacademy.hotel.exception.ErrorWrapper;
import com.tinqinacademy.hotel.exception.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.logging.ErrorManager;

public interface ErrorUtil {
    ErrorWrapper handle(MethodArgumentNotValidException ex, HttpStatus status);
    ErrorWrapper handle(NotFoundException ex, HttpStatus status);
}
