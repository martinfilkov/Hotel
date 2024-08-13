package com.tinqinacademy.hotel.core.converters;

import com.tinqinacademy.hotel.api.operations.system.updateroom.UpdateRoomOutput;
import com.tinqinacademy.hotel.persistence.entities.Room;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RoomToUpdateRoomOutputConverter implements Converter<Room, UpdateRoomOutput> {
    @Override
    public UpdateRoomOutput convert(Room input) {
        log.info("Start converting from Room to UpdateRoomOutput with input: {}", input);

        UpdateRoomOutput output = UpdateRoomOutput.builder()
                .id(input.getId().toString())
                .build();

        log.info("End converting from Room to UpdateRoomOutput with output: {}", output);
        return output;
    }
}
