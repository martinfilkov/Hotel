package com.tinqinacademy.hotel.api.operations.system.partialupdate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tinqinacademy.hotel.api.operations.annotations.bathroom.BathroomValidation;
import com.tinqinacademy.hotel.api.operations.annotations.bedsize.BedSizeValidation;
import com.tinqinacademy.hotel.api.operations.base.OperationInput;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
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
public class PartialUpdateRoomInput implements OperationInput {
    @NotBlank(message = "roomId cannot be null")
    @JsonIgnore
    @UUID(message = "UUID syntax required")
    private String roomId;

    @BedSizeValidation(optional = true)
    private List<String> bedSizes;

    @BathroomValidation(optional = true)
    private String bathRoomType;

    private String roomNumber;

    @PositiveOrZero(message = "Price must be positive or 0")
    private BigDecimal price;
}
