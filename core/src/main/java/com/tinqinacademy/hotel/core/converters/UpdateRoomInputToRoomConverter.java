package com.tinqinacademy.hotel.core.converters;

import com.tinqinacademy.hotel.api.operations.system.updateroom.UpdateRoomInput;
import com.tinqinacademy.hotel.persistence.entities.Room;
import com.tinqinacademy.hotel.persistence.models.BathroomType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
public class UpdateRoomInputToRoomConverter implements Converter<UpdateRoomInput, Room.RoomBuilder> {
    @Override
    public Room.RoomBuilder convert(UpdateRoomInput input) {
        log.info("Start converting from UpdateRoomInput to Room.RoomBuilder with input: {}", input);

        Room.RoomBuilder output = Room.builder()
                .id(UUID.fromString(input.getRoomId()))
                .bathroomType(BathroomType.getByCode(input.getBathRoomType()))
                .roomNumber(input.getRoomNumber())
                .price(input.getPrice());

        log.info("Start converting from UpdateRoomInput to Room.RoomBuilder with output: {}", output);
        return output;
    }
}
