package com.tinqinacademy.hotel.core.converters;

import com.tinqinacademy.hotel.api.operations.system.createroom.CreateRoomInput;
import com.tinqinacademy.hotel.persistence.entity.Room;
import com.tinqinacademy.hotel.persistence.model.BathroomType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CreateRoomInputToRoomConverter implements Converter<CreateRoomInput, Room.RoomBuilder> {

    @Override
    public Room.RoomBuilder convert(CreateRoomInput input) {
        log.info("Start converting from CreateRoomInput to Room.RoomBuilder with input: {}", input);

        Room.RoomBuilder output = Room.builder()
                .bathroomType(BathroomType.getByCode(input.getBathRoomType()))
                .floor(input.getFloor())
                .roomNumber(input.getRoomNumber())
                .price(input.getPrice());

        log.info("End converting from CreateRoomInput to Room.RoomBuilder with output: {}", output);
        return output;
    }
}
