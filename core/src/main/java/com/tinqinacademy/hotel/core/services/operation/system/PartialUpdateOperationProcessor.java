package com.tinqinacademy.hotel.core.services.operation.system;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import com.tinqinacademy.hotel.api.operations.base.Errors;
import com.tinqinacademy.hotel.api.operations.exception.NotFoundException;
import com.tinqinacademy.hotel.api.operations.system.partialupdate.PartialUpdateOperation;
import com.tinqinacademy.hotel.api.operations.system.partialupdate.PartialUpdateRoomInput;
import com.tinqinacademy.hotel.api.operations.system.partialupdate.PartialUpdateRoomOutput;
import com.tinqinacademy.hotel.core.services.operation.BaseOperationProcessor;
import com.tinqinacademy.hotel.core.utils.ErrorMapper;
import com.tinqinacademy.hotel.persistence.entity.Bed;
import com.tinqinacademy.hotel.persistence.entity.Room;
import com.tinqinacademy.hotel.persistence.model.BathroomType;
import com.tinqinacademy.hotel.persistence.model.BedSize;
import com.tinqinacademy.hotel.persistence.repositories.BedRepository;
import com.tinqinacademy.hotel.persistence.repositories.RoomRepository;
import io.vavr.control.Either;
import io.vavr.control.Try;
import jakarta.validation.Validator;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static io.vavr.API.*;
import static io.vavr.Predicates.instanceOf;

@Slf4j
@Service
public class PartialUpdateOperationProcessor extends BaseOperationProcessor implements PartialUpdateOperation {
    private final RoomRepository roomRepository;
    private final BedRepository bedRepository;
    private final ObjectMapper mapper;

    @Autowired
    public PartialUpdateOperationProcessor(RoomRepository roomRepository,
                                           BedRepository bedRepository,
                                           ConversionService conversionService,
                                           ObjectMapper mapper,
                                           Validator validator,
                                           ErrorMapper errorMapper) {
        super(conversionService, validator, errorMapper);
        this.roomRepository = roomRepository;
        this.bedRepository = bedRepository;
        this.mapper = mapper;
    }

    @SneakyThrows
    @Override
    public Either<Errors, PartialUpdateRoomOutput> process(PartialUpdateRoomInput input) {
        return validateInput(input)
                .flatMap(this::partialUpdateRoom);
    }

    private Either<Errors, PartialUpdateRoomOutput> partialUpdateRoom(PartialUpdateRoomInput input){
        return Try.of(() -> {
                    log.info("Start partialUpdateRoom input: {}", input);

                    checkIfBathroomIsValid(input);

                    List<BedSize> bedSizes = getBedSizesIfValid(input);

                    Room currentRoom = getIfRoomExists(input);

                    List<Bed> beds = bedRepository.findAllByBedSizeIn(bedSizes);

                    Room inputRoom = conversionService.convert(input, Room.RoomBuilder.class)
                            .bedSizes(beds)
                            .build();

                    JsonNode roomNode = mapper.valueToTree(currentRoom);
                    JsonNode inputNode = mapper.valueToTree(inputRoom);

                    JsonMergePatch patch = JsonMergePatch.fromJson(inputNode);
                    Room updatedRoom = mapper.treeToValue(patch.apply(roomNode), Room.class);

                    roomRepository.save(updatedRoom);

                    PartialUpdateRoomOutput output = conversionService.convert(updatedRoom, PartialUpdateRoomOutput.class);

                    log.info("End partialUpdateRoom output: {}", output);
                    return output;
                })
                .toEither()
                .mapLeft(throwable -> Match(throwable).of(
                        Case($(instanceOf(NotFoundException.class)), ex -> errorMapper.handleError(ex, HttpStatus.NOT_FOUND)),
                        Case($(), ex -> errorMapper.handleError(ex, HttpStatus.BAD_REQUEST))
                ));
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

    private List<BedSize> getBedSizesIfValid(PartialUpdateRoomInput input) {
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
