package com.tinqinacademy.hotel.api.operations.system.deleteroom;

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
public class DeleteRoomInput implements OperationInput {
    @NotBlank(message = "Id cannot be null")
    @UUID(message = "UUID syntax required")
    private String id;
}
