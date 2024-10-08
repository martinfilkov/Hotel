package com.tinqinacademy.hotel.api.operations.system.createroom;

import com.tinqinacademy.hotel.api.operations.annotations.bathroom.BathroomValidation;
import com.tinqinacademy.hotel.api.operations.annotations.bedsize.BedSizeValidation;
import com.tinqinacademy.hotel.api.operations.base.OperationInput;
import jakarta.validation.constraints.*;
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
    @NotNull(message = "Bed sizes cannot be null")
    @BedSizeValidation
    private List<String> bedSizes;

    @NotBlank(message = "Bathroom type cannot be null")
    @BathroomValidation
    private String bathRoomType;

    @Max(value = 20, message = "There cannot be more than 20 floors")
    @Min(value = 1, message = "There cannot be less than 1 floor")
    private Integer floor;

    @NotBlank(message = "Room number cannot be null")
    @Size(min = 2, max = 10)
    private String roomNumber;

    @PositiveOrZero(message = "Price must be positive")
    private BigDecimal price;
}
