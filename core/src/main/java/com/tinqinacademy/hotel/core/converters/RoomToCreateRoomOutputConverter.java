package com.tinqinacademy.hotel.core.converters;

import com.tinqinacademy.hotel.api.operations.system.createroom.CreateRoomOutput;
import com.tinqinacademy.hotel.persistence.entities.Room;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RoomToCreateRoomOutputConverter implements Converter<Room, CreateRoomOutput> {
    @Override
    public CreateRoomOutput convert(Room input) {
        log.info("Convert from Room to CreateRoomOutput with input: {}", input);

        CreateRoomOutput output = CreateRoomOutput.builder()
                .id(input.getId().toString())
                .build();

        log.info("Convert from Room to CreateRoomOutput with output: {}", output);
        return output;
    }
}
