package com.tinqinacademy.hotel.api.operations.system.deleteroom;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class DeleteRoomInput {
    @NotBlank(message = "Id cannot be null")
    private String id;
}
