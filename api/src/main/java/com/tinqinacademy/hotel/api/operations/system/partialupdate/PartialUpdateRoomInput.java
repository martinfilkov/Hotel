package com.tinqinacademy.hotel.api.operations.system.partialupdate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder(toBuilder = true)
@ToString
public class PartialUpdateRoomInput {
    @JsonIgnore
    private String roomId;

    @Max(value = 5, message = "You can't have more than 5 beds")
    private Integer bedCount;

    private String bedSize;

    private String bathRoomType;

    @Max(value = 20, message = "There cannot be more that 20 floors")
    private Integer floor;

    private String roomNumber;

    @PositiveOrZero(message = "Price must be positive or 0")
    private BigDecimal price;
}
