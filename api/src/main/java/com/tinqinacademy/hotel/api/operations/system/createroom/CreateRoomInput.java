package com.tinqinacademy.hotel.api.operations.system.createroom;

import com.tinqinacademy.hotel.api.operations.annotations.bathroom.BathroomValidation;
import com.tinqinacademy.hotel.api.operations.annotations.bedsize.BedSizeValidation;
import com.tinqinacademy.hotel.api.operations.base.OperationInput;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class CreateRoomInput implements OperationInput {
    @BedSizeValidation
    @NotNull(message = "Bed sizes cannot be null")
    private List<String> bedSizes;

    @BathroomValidation
    @NotBlank(message = "Bathroom type cannot be null")
    private String bathRoomType;

    @Max(value = 20, message = "There cannot be more than 20 floors")
    private Integer floor;

    @NotBlank(message = "Room number cannot be null")
    private String roomNumber;

    @PositiveOrZero(message = "Price must be positive")
    private BigDecimal price;
}
