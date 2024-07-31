package com.tinqinacademy.hotel.api.operations.hotel.getroomids;

import com.tinqinacademy.hotel.api.operations.base.OperationOutput;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class GetRoomIdsOutput implements OperationOutput {
    List<String> ids;
}
