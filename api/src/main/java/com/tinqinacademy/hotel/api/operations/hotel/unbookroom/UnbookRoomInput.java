package com.tinqinacademy.hotel.api.operations.hotel.unbookroom;

import com.tinqinacademy.hotel.api.operations.base.OperationInput;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.validator.constraints.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class UnbookRoomInput implements OperationInput {
    @NotBlank(message = "Booking id cannot be null")
    @UUID(message = "UUID syntax required")
    private String bookingId;
}
