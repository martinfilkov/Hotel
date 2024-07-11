package com.tinqinacademy.hotel.api.operations.exception;

import lombok.*;

import java.util.Date;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class ErrorWrapper {
    private List<ErrorResponse> errors;
    private Date timestamp;
}
