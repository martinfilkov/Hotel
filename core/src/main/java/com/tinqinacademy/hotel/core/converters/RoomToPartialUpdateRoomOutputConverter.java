package com.tinqinacademy.hotel.core.converters;

import com.tinqinacademy.hotel.api.operations.system.partialupdate.PartialUpdateRoomOutput;
import com.tinqinacademy.hotel.persistence.entity.Room;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RoomToPartialUpdateRoomOutputConverter implements Converter<Room, PartialUpdateRoomOutput> {
    @Override
    public PartialUpdateRoomOutput convert(Room input) {
        log.info("Start converting from Room to PartialUpdateRoomOutput with input: {}", input);

        PartialUpdateRoomOutput output = PartialUpdateRoomOutput.builder()
                .id(input.getId().toString())
                .build();

        log.info("End converting from Room to PartialUpdateRoomOutput with output: {}", output);
        return output;
    }
}
