package com.tinqinacademy.hotel.api.operations.system.partialupdate;

import com.tinqinacademy.hotel.api.operations.base.OperationOutput;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class PartialUpdateRoomOutput implements OperationOutput {
    private String id;
}
