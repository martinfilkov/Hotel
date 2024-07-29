package com.tinqinacademy.hotel.core.services.operation.system;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import com.tinqinacademy.hotel.api.operations.exception.NotFoundException;
import com.tinqinacademy.hotel.api.operations.system.partialupdate.PartialUpdateProcess;
import com.tinqinacademy.hotel.api.operations.system.partialupdate.PartialUpdateRoomInput;
import com.tinqinacademy.hotel.api.operations.system.partialupdate.PartialUpdateRoomOutput;
import com.tinqinacademy.hotel.persistence.entity.Room;
import com.tinqinacademy.hotel.persistence.model.BathroomType;
import com.tinqinacademy.hotel.persistence.model.BedSize;
import com.tinqinacademy.hotel.persistence.repositories.BedRepository;
import com.tinqinacademy.hotel.persistence.repositories.RoomRepository;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class PartialUpdateOperation implements PartialUpdateProcess {
    private final RoomRepository roomRepository;
    private final BedRepository bedRepository;
    private final ConversionService conversionService;
    private final ObjectMapper mapper;

    @Autowired
    public PartialUpdateOperation(RoomRepository roomRepository, BedRepository bedRepository, ConversionService conversionService, ObjectMapper mapper) {
        this.roomRepository = roomRepository;
        this.bedRepository = bedRepository;
        this.conversionService = conversionService;
        this.mapper = mapper;
    }

    @SneakyThrows
    @Override
    public PartialUpdateRoomOutput process(PartialUpdateRoomInput input) {
        log.info("Start partialUpdateRoom input: {}", input);

        checkIfBathroomIsValid(input);

        checkIfBedSizeIsValid(input);

        Room currentRoom = getIfRoomExists(input);
        ;

        Room inputRoom = conversionService.convert(input, Room.RoomBuilder.class)
                .bedSizes(input.getBedSizes() != null ?
                        input.getBedSizes().stream().map(bed ->
                                bedRepository.findByBedSize(BedSize.getByCode(bed)).orElseThrow()
                        ).toList() : null)
                .build();

        JsonNode roomNode = mapper.valueToTree(currentRoom);
        JsonNode inputNode = mapper.valueToTree(inputRoom);

        JsonMergePatch patch = JsonMergePatch.fromJson(inputNode);
        Room updatedRoom = mapper.treeToValue(patch.apply(roomNode), Room.class);

        roomRepository.save(updatedRoom);

        PartialUpdateRoomOutput output = conversionService.convert(updatedRoom, PartialUpdateRoomOutput.class);

        log.info("End partialUpdateRoom output: {}", output);
        return output;
    }

    private Room getIfRoomExists(PartialUpdateRoomInput input) {
        log.info("Try to get room with id: {}", input.getRoomId());

        Optional<Room> roomOptional = roomRepository.findById(UUID.fromString(input.getRoomId()));
        if (roomOptional.isEmpty()) {
            throw new NotFoundException("Room with id " + input.getRoomId() + " not found");
        }

        log.info("Room with id {} exists", input.getRoomId());
        return roomOptional.get();
    }

    private void checkIfBathroomIsValid(PartialUpdateRoomInput input) {
        log.info("Check if bathroom type is valid");
        if (BathroomType.getByCode(input.getBathRoomType()).equals(BathroomType.UNKNOWN)
                && input.getBathRoomType() != null) {
            throw new NotFoundException("Bathroom type " + input.getBathRoomType() + " not found");
        }
        log.info("Bathroom type is valid");
    }

    private void checkIfBedSizeIsValid(PartialUpdateRoomInput input) {
        log.info("Check if each bed size is valid and not null");
        if (input.getBedSizes() != null) {
            input.getBedSizes().forEach(this::checkIfBedSizeIsValid);
        }
        log.info("Bed sizes are valid");
    }

    private void checkIfBedSizeIsValid(String bedSize) {
        if (BedSize.getByCode(bedSize).equals(BedSize.UNKNOWN)) {
            throw new NotFoundException("Bed size " + bedSize + " not found");
        }
    }
}
