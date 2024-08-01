package com.tinqinacademy.hotel.api.operations.system.registervisitor;

import com.tinqinacademy.hotel.api.operations.base.OperationInput;
import jakarta.validation.Valid;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class RegisterVisitorInputList implements OperationInput {
    @FutureOrPresent(message = "Date cannot be in the past")
    private LocalDate startDate;

    @FutureOrPresent(message = "Date cannot be in the past")
    private LocalDate endDate;

    @NotBlank(message = "Room number is required")
    private String roomNumber;

    @NotNull(message = "visitors cannot be null")
    private List<@Valid RegisterVisitorInput> visitors;
}
