package com.tinqinacademy.hotel.core.converters;

import com.tinqinacademy.hotel.api.operations.hotel.getroomids.GetRoomIdsOutput;
import com.tinqinacademy.hotel.persistence.entity.Room;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class RoomToGetRoomIdsOutputConverter implements Converter<List<Room>, GetRoomIdsOutput> {
    @Override
    public GetRoomIdsOutput convert(List<Room> input) {
        log.info("Start converting from List<Room> to GetRoomIdsOutput with input: {}", input);

        GetRoomIdsOutput output = GetRoomIdsOutput.builder()
                .ids(input.stream()
                        .map(room -> room.getId().toString()).toList()
                )
                .build();

        log.info("End converting from List<Room> to GetRoomIdsOutput with output: {}", output);
        return output;
    }
}
