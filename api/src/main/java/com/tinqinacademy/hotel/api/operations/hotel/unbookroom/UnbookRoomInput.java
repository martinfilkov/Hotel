package com.tinqinacademy.hotel.api.operations.hotel.unbookroom;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class UnbookRoomInput {
    @NotBlank(message = "Booking id cannot be null")
    private String bookingId;
}
