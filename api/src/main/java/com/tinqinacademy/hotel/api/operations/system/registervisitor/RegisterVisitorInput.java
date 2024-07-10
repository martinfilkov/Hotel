package com.tinqinacademy.hotel.api.operations.system.registervisitor;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class RegisterVisitorInput {
    @FutureOrPresent(message = "Date cannot be in the past")
    private LocalDate startDate;

    @FutureOrPresent(message = "Date cannot be in the past")
    private LocalDate endDate;

    @NotBlank(message = "First name cannot be null")
    private String firstName;

    @NotBlank(message = "Last name cannot be null")
    private String lastName;

    @Size(min = 10, max = 10)
    private String phone;

    @NotBlank(message = "Card number cannot be null")
    private String idCardNumber;

    @NotBlank(message = "Card validity cannot be null")
    private String idCardValidity;

    @NotBlank(message = "Card issue authority cannot be null")
    private String idCardIssueAuthority;

    @NotBlank(message = "Card issue cannot be null")
    private String idCardIssueDate;
}
