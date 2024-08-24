package com.tinqinacademy.hotel.api.operations.system.registervisitor;

import com.tinqinacademy.hotel.api.operations.base.OperationInput;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class RegisterVisitorInput implements OperationInput {
    @NotBlank(message = "First name cannot be null")
    @Size(min = 2, max = 50)
    @Pattern(regexp = "^\\p{Alpha}*$", message = "Name should contain only alphabets")
    private String firstName;

    @NotBlank(message = "Last name cannot be null")
    @Size(min = 2, max = 50)
    @Pattern(regexp = "^\\p{Alpha}*$", message = "Name should contain only alphabets")
    private String lastName;

    @Size(min = 10, max = 10)
    private String phone;

    @NotBlank(message = "Card number cannot be null")
    @Size(min = 2, max = 50)
    private String idCardNumber;

    @NotBlank(message = "Card validity cannot be null")
    @Size(min = 2, max = 50)
    private String idCardValidity;

    @NotBlank(message = "Card issue authority cannot be null")
    @Size(min = 2, max = 50)
    private String idCardIssueAuthority;

    @NotBlank(message = "Card issue cannot be null")
    @Size(min = 2, max = 50)
    private String idCardIssueDate;

    @PastOrPresent(message = "You cannot be born in the future")
    private LocalDate birthDate;
}
