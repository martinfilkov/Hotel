package com.tinqinacademy.hotel.api.operations.hotel.roombyid;

import com.tinqinacademy.hotel.api.operations.base.OperationInput;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.validator.constraints.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class RoomByIdInput implements OperationInput {
    @NotBlank(message = "Id cannot be empty")
    @UUID(message = "UUID syntax required")
    private String id;
}
