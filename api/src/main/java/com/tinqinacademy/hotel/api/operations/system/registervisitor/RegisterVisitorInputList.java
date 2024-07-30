package com.tinqinacademy.hotel.api.operations.system.registervisitor;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class RegisterVisitorInputList {
    @FutureOrPresent(message = "Date cannot be in the past")
    private LocalDate startDate;

    @FutureOrPresent(message = "Date cannot be in the past")
    private LocalDate endDate;

    @NotBlank(message = "Room number is required")
    private String roomNumber;

    private List<RegisterVisitorInput> visitors;
}
