package com.tinqinacademy.hotel.api.operations.system.updateroom;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder(toBuilder = true)
@ToString
public class UpdateRoomInput {
    @JsonIgnore
    private String roomId;

    @Max(value = 5, message = "You can't have more than 5 beds")
    private Integer bedCount;

    @NotBlank(message = "Bed size cannot be empty")
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
