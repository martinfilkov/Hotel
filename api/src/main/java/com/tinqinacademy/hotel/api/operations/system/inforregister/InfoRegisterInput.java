package com.tinqinacademy.hotel.api.operations.system.inforregister;

import com.tinqinacademy.hotel.api.operations.base.OperationInput;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class InfoRegisterInput implements OperationInput {
    @FutureOrPresent(message = "Date cannot be in the past")
    private LocalDate startDate;

    @FutureOrPresent(message = "Date cannot be in the past")
    private LocalDate endDate;

    private String firstName;

    private String lastName;

    @Size(min = 10, max = 10)
    private String phone;

    private String idCardNumber;

    private String idCardValidity;

    private String idCardIssueAuthority;

    private String idCardIssueDate;

    private String roomNumber;
}
