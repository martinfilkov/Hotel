package com.tinqinacademy.hotel.api.operations.system.createroom;

import com.tinqinacademy.hotel.api.operations.base.OperationOutput;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class CreateRoomOutput implements OperationOutput {
    private String id;
}
