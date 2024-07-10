package com.tinqinacademy.hotel.api.operations.hotel.roombyid;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class RoomByIdInput {
    @NotBlank(message = "Id cannot be empty")
    private String id;
}
