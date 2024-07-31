package com.tinqinacademy.hotel.api.operations.exception;

import com.tinqinacademy.hotel.api.operations.base.Errors;
import lombok.*;
import org.springframework.http.HttpStatusCode;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class ErrorResponse {
    private String message;
}
