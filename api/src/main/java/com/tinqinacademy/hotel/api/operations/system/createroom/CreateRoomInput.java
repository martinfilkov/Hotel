package com.tinqinacademy.hotel.api.operations.system.createroom;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class CreateRoomInput {
    private List<String> bedSizes;

    @NotBlank(message = "Bathroom type cannot be null")
    private String bathRoomType;

    @Max(value = 20, message = "There cannot be more that 20 floors")
    private Integer floor;

    @NotBlank(message = "Room number cannot be null")
    private String roomNumber;

    @PositiveOrZero(message = "Price must be positive")
    private BigDecimal price;
}
