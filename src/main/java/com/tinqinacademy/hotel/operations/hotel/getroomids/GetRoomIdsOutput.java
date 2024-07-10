package com.tinqinacademy.hotel.operations.hotel.getroomids;

import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class GetRoomIdsOutput {
    List<String> ids;
}
