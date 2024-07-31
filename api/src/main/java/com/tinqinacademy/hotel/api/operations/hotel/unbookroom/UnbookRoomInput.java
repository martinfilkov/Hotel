package com.tinqinacademy.hotel.api.operations.hotel.unbookroom;

import com.tinqinacademy.hotel.api.operations.base.OperationInput;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class UnbookRoomInput implements OperationInput {
    @NotBlank(message = "Booking id cannot be null")
    private String bookingId;
}
