package com.tinqinacademy.hotel.api.operations.system.updateroom;

import com.tinqinacademy.hotel.api.operations.base.OperationOutput;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class UpdateRoomOutput implements OperationOutput {
    private String id;
}
