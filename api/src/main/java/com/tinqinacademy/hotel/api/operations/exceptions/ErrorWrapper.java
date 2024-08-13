package com.tinqinacademy.hotel.api.operations.exceptions;

import com.tinqinacademy.hotel.api.operations.base.Errors;
import lombok.*;

import java.sql.Timestamp;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class ErrorWrapper implements Errors {
    private List<ErrorResponse> errors;
    private Timestamp timestamp;
    private Integer code;
}
