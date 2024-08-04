package com.tinqinacademy.hotel.api.operations.exception;

import com.tinqinacademy.hotel.api.operations.base.Errors;
import lombok.*;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Date;
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
