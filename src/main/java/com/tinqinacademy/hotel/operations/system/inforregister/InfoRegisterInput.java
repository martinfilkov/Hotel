package com.tinqinacademy.hotel.operations.system.inforregister;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class InfoRegisterInput {
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

    @NotBlank(message = "Room number cannot be null")
    private String roomNumber;
}
