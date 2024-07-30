package com.tinqinacademy.hotel.core.services.operation.system;

import com.tinqinacademy.hotel.api.operations.exception.NotFoundException;
import com.tinqinacademy.hotel.api.operations.system.createroom.CreateRoomInput;
import com.tinqinacademy.hotel.api.operations.system.createroom.CreateRoomOutput;
import com.tinqinacademy.hotel.api.operations.system.createroom.CreateRoomProcess;
import com.tinqinacademy.hotel.api.operations.system.partialupdate.PartialUpdateRoomInput;
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

        List<BedSize> bedSizes = getBedSizesIfValid(input);

        Room room = conversionService.convert(input, Room.RoomBuilder.class)
                .bedSizes(bedRepository.findAllByBedSizeIn(bedSizes))
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

    private List<BedSize> getBedSizesIfValid(CreateRoomInput input) {
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
