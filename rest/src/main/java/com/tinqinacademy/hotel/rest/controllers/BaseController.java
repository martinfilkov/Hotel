package com.tinqinacademy.hotel.rest.controllers;

import com.tinqinacademy.hotel.api.operations.base.Errors;
import io.vavr.control.Either;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public abstract class BaseController {
    public <T> ResponseEntity<?> handleResponse(Either<Errors, T> either, HttpStatus status) {
        if (either.isLeft()) {
            Errors errors = either.getLeft();
            return new ResponseEntity<>(errors, HttpStatusCode.valueOf(errors.getCode()));
        }
        return new ResponseEntity<>(either.get(), status);
    }
}
