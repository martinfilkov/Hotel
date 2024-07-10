package com.tinqinacademy.hotel.rest.exception;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class ErrorResponse {
    private int status;
    private String message;
}
