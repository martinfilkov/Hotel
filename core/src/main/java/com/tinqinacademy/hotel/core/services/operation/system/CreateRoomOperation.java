package com.tinqinacademy.hotel.core.services.operation.system;

import com.tinqinacademy.hotel.api.operations.exception.NotFoundException;
import com.tinqinacademy.hotel.api.operations.system.createroom.CreateRoomInput;
import com.tinqinacademy.hotel.api.operations.system.createroom.CreateRoomOutput;
import com.tinqinacademy.hotel.api.operations.system.createroom.CreateRoomProcess;
import com.tinqinacademy.hotel.persistence.entity.Room;
import com.tinqinacademy.hotel.persistence.model.BathroomType;
import com.tinqinacademy.hotel.persistence.model.BedSize;
import com.tinqinacademy.hotel.persistence.repositories.BedRepository;
import com.tinqinacademy.hotel.persistence.repositories.RoomRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CreateRoomOperation implements CreateRoomProcess {
    private final RoomRepository roomRepository;
    private final BedRepository bedRepository;
    private final ConversionService conversionService;

    @Autowired
    public CreateRoomOperation(RoomRepository roomRepository, BedRepository bedRepository, ConversionService conversionService) {
        this.roomRepository = roomRepository;
        this.bedRepository = bedRepository;
        this.conversionService = conversionService;
    }

    @Override
    public CreateRoomOutput process(CreateRoomInput input) {
        log.info("Start createRoom input: {}", input);

        checkIfBathroomIsValid(input);

        input.getBedSizes().forEach(this::checkIfBedSizeIsValid);

        Room room = conversionService.convert(input, Room.RoomBuilder.class)
                .bedSizes(input.getBedSizes().stream().map(bed ->
                                bedRepository.findByBedSize(BedSize.getByCode(bed)).orElseThrow())
                        .toList()
                )
                .build();

        Room savedRoom = roomRepository.save(room);

        CreateRoomOutput output = conversionService.convert(savedRoom, CreateRoomOutput.class);

        log.info("End createRoom output: {}", output);
        return output;
    }

    private void checkIfBathroomIsValid(CreateRoomInput input){
        log.info("Check if bathroom type is valid");
        if (BathroomType.getByCode(input.getBathRoomType()).equals(BathroomType.UNKNOWN)
                && input.getBathRoomType() != null) {
            throw new NotFoundException("Bathroom type " + input.getBathRoomType() + " not found");
        }
        log.info("Bathroom type is valid");
    }

    private void checkIfBedSizeIsValid(String bedSize){
        if (BedSize.getByCode(bedSize).equals(BedSize.UNKNOWN)) {
            throw new NotFoundException("Bed size " + bedSize + " not found");
        }
    }
}
