package com.tinqinacademy.hotel.operations.system.createroom;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class CreateRoomInput {
    @Max(value = 5, message = "You can't have more than 5 beds")
    private Integer bedCount;

    @NotBlank(message = "Bed size cannot be null")
    private String bedSize;

    @NotBlank(message = "Bathroom type cannot be null")
    private String bathRoomType;

    @Max(value = 20, message = "There cannot be more that 20 floors")
    private Integer floor;

    @NotBlank(message = "Room number cannot be null")
    private String roomNumber;

    @PositiveOrZero(message = "Price must be positive")
    private BigDecimal price;
}
