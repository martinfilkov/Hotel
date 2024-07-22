package com.tinqinacademy.hotel.api.operations.system.partialupdate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder(toBuilder = true)
@ToString
public class PartialUpdateRoomInput {
    @JsonIgnore
    private String roomId;

    private List<String> bedSizes;

    private String bathRoomType;

    private String roomNumber;

    @PositiveOrZero(message = "Price must be positive or 0")
    private BigDecimal price;
}
