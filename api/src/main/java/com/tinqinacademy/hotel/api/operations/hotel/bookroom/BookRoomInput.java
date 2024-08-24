package com.tinqinacademy.hotel.api.operations.hotel.bookroom;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tinqinacademy.hotel.api.operations.base.OperationInput;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.validator.constraints.UUID;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder(toBuilder = true)
@ToString
public class BookRoomInput implements OperationInput {
    @NotBlank(message = "roomId cannot be null")
    @JsonIgnore
    @UUID(message = "UUID syntax required")
    private String roomId;

    @FutureOrPresent(message = "Date cannot be in the past")
    private LocalDate startDate;

    @FutureOrPresent(message = "Date cannot be in the past")
    private LocalDate endDate;

    @NotBlank(message = "First name cannot be empty")
    @Size(min = 2, max = 50)
    @Pattern(regexp = "^\\p{Alpha}*$", message = "Name should contain only alphabets")
    private String firstName;

    @NotBlank(message = "Last name cannot be empty")
    @Size(min = 2, max = 50)
    @Pattern(regexp = "^\\p{Alpha}*$", message = "Name should contain only alphabets")
    private String lastName;

    @Size(min = 10, max = 10, message = "Phone must be 10 characters")
    private String phone;

    @NotBlank(message = "User id cannot be empty")
    @UUID(message = "UUID syntax required")
    private String userId;
}
