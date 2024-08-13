package com.tinqinacademy.hotel.core.converters;

import com.tinqinacademy.hotel.api.operations.system.partialupdate.PartialUpdateRoomInput;
import com.tinqinacademy.hotel.persistence.entities.Room;
import com.tinqinacademy.hotel.persistence.models.BathroomType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PartialUpdateRoomInputToRoomConverter implements Converter<PartialUpdateRoomInput, Room.RoomBuilder> {
    @Override
    public Room.RoomBuilder convert(PartialUpdateRoomInput input) {
        log.info("Start converting from PartialUpdateRoomInput to Room.RoomBuilder with input: {}", input);

        Room.RoomBuilder output = Room.builder()
                .price(input.getPrice())
                .roomNumber(input.getRoomNumber())
                .bathroomType(!BathroomType.getByCode(input.getBathRoomType()).equals(BathroomType.UNKNOWN) ?
                        BathroomType.getByCode(input.getBathRoomType()) : null);

        log.info("End converting from PartialUpdateRoomInput to Room.RoomBuilder with output: {}", output);
        return output;
    }
}
