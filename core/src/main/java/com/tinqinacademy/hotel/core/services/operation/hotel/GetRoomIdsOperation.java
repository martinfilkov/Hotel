package com.tinqinacademy.hotel.core.services.operation.hotel;

import com.tinqinacademy.hotel.api.operations.hotel.getroomids.GetRoomIdsInput;
import com.tinqinacademy.hotel.api.operations.hotel.getroomids.GetRoomIdsOutput;
import com.tinqinacademy.hotel.api.operations.hotel.getroomids.GetRoomIdsProcess;
import com.tinqinacademy.hotel.persistence.entity.Room;
import com.tinqinacademy.hotel.persistence.model.BathroomType;
import com.tinqinacademy.hotel.persistence.repositories.RoomRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class GetRoomIdsOperation implements GetRoomIdsProcess {
    private final RoomRepository roomRepository;
    private final ConversionService conversionService;

    @Autowired
    public GetRoomIdsOperation(RoomRepository roomRepository, ConversionService conversionService) {
        this.roomRepository = roomRepository;
        this.conversionService = conversionService;
    }

    @Override
    public GetRoomIdsOutput process(GetRoomIdsInput input) {
        log.info("Start getRoomIds input: {}", input);
        List<Room> rooms = roomRepository.findAvailableRooms(input.getStartDate(), input.getEndDate());

        List<Room> availableRooms = rooms.stream()
                .filter(room -> input.getBedSize()
                        .map(size -> room.getBedSizes().stream()
                                .anyMatch(bed -> bed.getBedSize().toString().equals(size)))
                        .orElse(true))
                .filter(room ->
                        input.getBathroomType()
                                .map(type -> BathroomType.getByCode(type) == room.getBathroomType())
                                .orElse(true)
                )
                .toList();

        GetRoomIdsOutput output = conversionService.convert(availableRooms, GetRoomIdsOutput.class);

        log.info("End getRoomIds output: {}", output);
        return output;
    }
}
