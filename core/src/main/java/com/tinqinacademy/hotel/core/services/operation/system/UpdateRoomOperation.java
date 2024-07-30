package com.tinqinacademy.hotel.core.services.operation.system;

import com.tinqinacademy.hotel.api.operations.exception.NotFoundException;
import com.tinqinacademy.hotel.api.operations.system.partialupdate.PartialUpdateRoomInput;
import com.tinqinacademy.hotel.api.operations.system.updateroom.UpdateRoomInput;
import com.tinqinacademy.hotel.api.operations.system.updateroom.UpdateRoomOutput;
import com.tinqinacademy.hotel.api.operations.system.updateroom.UpdateRoomProcess;
import com.tinqinacademy.hotel.persistence.entity.Room;
import com.tinqinacademy.hotel.persistence.model.BathroomType;
import com.tinqinacademy.hotel.persistence.model.BedSize;
import com.tinqinacademy.hotel.persistence.repositories.BedRepository;
import com.tinqinacademy.hotel.persistence.repositories.RoomRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class UpdateRoomOperation implements UpdateRoomProcess {
    private final RoomRepository roomRepository;
    private final ConversionService conversionService;
    private final BedRepository bedRepository;

    @Autowired
    public UpdateRoomOperation(RoomRepository roomRepository, ConversionService conversionService, BedRepository bedRepository) {
        this.roomRepository = roomRepository;
        this.conversionService = conversionService;
        this.bedRepository = bedRepository;
    }

    @Override
    public UpdateRoomOutput process(UpdateRoomInput input) {
        log.info("Start updateRoom input: {}", input);

        Room currentRoom = getIfRoomExists(input);

        checkIfBathroomIsValid(input);

        List<BedSize> bedSizes = getBedSizesIfValid(input);

        Room room = conversionService.convert(input, Room.RoomBuilder.class)
                .bedSizes(bedRepository.findAllByBedSizeIn(bedSizes))
                .floor(currentRoom.getFloor())
                .build();

        Room updatedRoom = roomRepository.save(room);

        UpdateRoomOutput output = conversionService.convert(updatedRoom, UpdateRoomOutput.class);

        log.info("End updateRoom output: {}", output);
        return output;
    }

    private void checkIfBathroomIsValid(UpdateRoomInput input){
        log.info("Check if bathroom type is valid");
        if (BathroomType.getByCode(input.getBathRoomType()).equals(BathroomType.UNKNOWN)
                && input.getBathRoomType() != null) {
            throw new NotFoundException("Bathroom type " + input.getBathRoomType() + " not found");
        }
        log.info("Bathroom type is valid");
    }

    private Room getIfRoomExists(UpdateRoomInput input){
        log.info("Try to get room with id: {}", input.getRoomId());

        Optional<Room> roomOptional = roomRepository.findById(UUID.fromString(input.getRoomId()));
        if (roomOptional.isEmpty()) {
            throw new NotFoundException("Room with id " + input.getRoomId() + " not found");
        }

        log.info("Room with id {} exists", input.getRoomId());
        return roomOptional.get();
    }

    private List<BedSize> getBedSizesIfValid(UpdateRoomInput input) {
        log.info("Check if each bed size is valid and not null");
        List<BedSize> bedSizes = new ArrayList<>();
        if (input.getBedSizes() != null
                && !ObjectUtils.isEmpty(input.getBedSizes())) {
            bedSizes = input.getBedSizes()
                    .stream()
                    .map(this::checkIfBedSizeIsValid)
                    .toList();
        }
        log.info("Bed sizes are valid");
        return bedSizes;
    }

    private BedSize checkIfBedSizeIsValid(String bedSize) {
        BedSize bed = BedSize.getByCode(bedSize);
        if (bed.equals(BedSize.UNKNOWN)) {
            throw new NotFoundException("Bed size " + bedSize + " not found");
        } else {
            return bed;
        }
    }
}
