package com.tinqinacademy.hotel.api.operations.system.updateroom;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tinqinacademy.hotel.api.operations.annotations.bathroom.BathroomValidation;
import com.tinqinacademy.hotel.api.operations.annotations.bedsize.BedSizeValidation;
import com.tinqinacademy.hotel.api.operations.base.OperationInput;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.validator.constraints.UUID;

import java.math.BigDecimal;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder(toBuilder = true)
@ToString
public class UpdateRoomInput implements OperationInput {
    @NotBlank(message = "roomId cannot be null")
    @JsonIgnore
    @UUID(message = "UUID syntax required")
    private String roomId;

    @NotNull(message = "Bed sizes cannot be null")
    @BedSizeValidation
    private List<String> bedSizes;

    @NotBlank(message = "Bathroom type cannot be null")
    @BathroomValidation
    private String bathRoomType;

    @NotBlank(message = "Room number cannot be null")
    @Size(min = 2, max = 10)
    private String roomNumber;

    @PositiveOrZero(message = "Price must be positive")
    private BigDecimal price;
}
