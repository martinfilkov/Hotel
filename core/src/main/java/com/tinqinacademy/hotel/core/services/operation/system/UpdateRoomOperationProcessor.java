package com.tinqinacademy.hotel.core.services.operation.system;

import com.tinqinacademy.hotel.api.operations.base.Errors;
import com.tinqinacademy.hotel.api.operations.exception.NotFoundException;
import com.tinqinacademy.hotel.api.operations.system.updateroom.UpdateRoomInput;
import com.tinqinacademy.hotel.api.operations.system.updateroom.UpdateRoomOperation;
import com.tinqinacademy.hotel.api.operations.system.updateroom.UpdateRoomOutput;
import com.tinqinacademy.hotel.core.services.operation.BaseOperationProcessor;
import com.tinqinacademy.hotel.core.utils.ErrorMapper;
import com.tinqinacademy.hotel.persistence.entity.Room;
import com.tinqinacademy.hotel.persistence.model.BathroomType;
import com.tinqinacademy.hotel.persistence.model.BedSize;
import com.tinqinacademy.hotel.persistence.repositories.BedRepository;
import com.tinqinacademy.hotel.persistence.repositories.RoomRepository;
import io.vavr.control.Either;
import io.vavr.control.Try;
import jakarta.validation.Validator;
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
public class UpdateRoomOperationProcessor extends BaseOperationProcessor implements UpdateRoomOperation {
    private final RoomRepository roomRepository;
    private final BedRepository bedRepository;

    @Autowired
    public UpdateRoomOperationProcessor(RoomRepository roomRepository,
                                        ConversionService conversionService,
                                        BedRepository bedRepository,
                                        Validator validator,
                                        ErrorMapper errorMapper) {
        super(conversionService, validator, errorMapper);
        this.roomRepository = roomRepository;
        this.bedRepository = bedRepository;
    }

    @Override
    public Either<Errors, UpdateRoomOutput> process(UpdateRoomInput input) {
        return validateInput(input)
                .flatMap(this::updateRoom);
    }

    private Either<Errors, UpdateRoomOutput> updateRoom(UpdateRoomInput input) {
        return Try.of(() -> {
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
                })
                .toEither()
                .mapLeft(throwable -> Match(throwable).of(
                        Case($(instanceOf(NotFoundException.class)), ex -> errorMapper.handleError(ex, HttpStatus.NOT_FOUND)),
                        Case($(), ex -> errorMapper.handleError(ex, HttpStatus.BAD_REQUEST))
                ));
    }

    private void checkIfBathroomIsValid(UpdateRoomInput input) {
        log.info("Check if bathroom type is valid");
        if (BathroomType.getByCode(input.getBathRoomType()).equals(BathroomType.UNKNOWN)
                && input.getBathRoomType() != null) {
            throw new NotFoundException(String.format("Bathroom type %s not found", input.getBathRoomType()));
        }
        log.info("Bathroom type is valid");
    }

    private Room getIfRoomExists(UpdateRoomInput input) {
        log.info("Try to get room with id: {}", input.getRoomId());

        Optional<Room> roomOptional = roomRepository.findById(UUID.fromString(input.getRoomId()));
        if (roomOptional.isEmpty()) {
            throw new NotFoundException(String.format("Room with id %s not found", input.getRoomId()));
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
            throw new NotFoundException(String.format("Bed size %s not found", bedSize));
        } else {
            return bed;
        }
    }
}
