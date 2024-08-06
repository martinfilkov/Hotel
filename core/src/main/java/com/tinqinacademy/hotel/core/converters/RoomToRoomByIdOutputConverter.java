package com.tinqinacademy.hotel.core.converters;

import com.tinqinacademy.hotel.api.operations.hotel.roombyid.RoomByIdOutput;
import com.tinqinacademy.hotel.persistence.entity.Bed;
import com.tinqinacademy.hotel.persistence.entity.Room;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RoomToRoomByIdOutputConverter implements Converter<Room, RoomByIdOutput.RoomByIdOutputBuilder> {
    @Override
    public RoomByIdOutput.RoomByIdOutputBuilder convert(Room input) {
        log.info("Start converting from Room to RoomByIdOutput.RoomByIdOutputBuilder with input: {}", input);

        RoomByIdOutput.RoomByIdOutputBuilder output = RoomByIdOutput.builder()
                .id(input.getId().toString())
                .floor(input.getFloor())
                .bathroomType(input.getBathroomType().toString())
                .bedSizes(input.getBedSizes().stream().map(Bed::toString).toList())
                .price(input.getPrice());

        log.info("End converting from Room to RoomByIdOutput.RoomByIdOutputBuilder with output: {}", output);
        return output;
    }
}
