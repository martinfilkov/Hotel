package com.tinqinacademy.hotel.core.services.operations.system;

import com.tinqinacademy.hotel.api.operations.base.Errors;
import com.tinqinacademy.hotel.api.operations.exceptions.NotAvailableException;
import com.tinqinacademy.hotel.api.operations.exceptions.NotFoundException;
import com.tinqinacademy.hotel.api.operations.system.createroom.CreateRoomInput;
import com.tinqinacademy.hotel.api.operations.system.createroom.CreateRoomOperation;
import com.tinqinacademy.hotel.api.operations.system.createroom.CreateRoomOutput;
import com.tinqinacademy.hotel.core.services.operations.BaseOperationProcessor;
import com.tinqinacademy.hotel.core.utils.ErrorMapper;
import com.tinqinacademy.hotel.persistence.entities.Room;
import com.tinqinacademy.hotel.persistence.models.BedSize;
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

import static io.vavr.API.*;
import static io.vavr.Predicates.instanceOf;

@Slf4j
@Service
public class CreateRoomOperationProcessor extends BaseOperationProcessor implements CreateRoomOperation {
    private final RoomRepository roomRepository;
    private final BedRepository bedRepository;

    @Autowired
    public CreateRoomOperationProcessor(RoomRepository roomRepository,
                                        BedRepository bedRepository,
                                        ConversionService conversionService,
                                        Validator validator,
                                        ErrorMapper errorMapper) {
        super(conversionService, validator, errorMapper);
        this.roomRepository = roomRepository;
        this.bedRepository = bedRepository;
    }

    @Override
    public Either<Errors, CreateRoomOutput> process(CreateRoomInput input) {
        return validateInput(input)
                .flatMap(validated -> createRoom(input));
    }

    private Either<Errors, CreateRoomOutput> createRoom(CreateRoomInput input){
        return Try.of(() -> {
                    log.info("Start createRoom input: {}", input);
                    checkIfRoomExists(input);

                    List<BedSize> bedSizes = getBedSizes(input);

                    Room room = conversionService.convert(input, Room.RoomBuilder.class)
                            .bedSizes(bedRepository.findAllByBedSizeIn(bedSizes))
                            .build();

                    Room savedRoom = roomRepository.save(room);

                    CreateRoomOutput output = conversionService.convert(savedRoom, CreateRoomOutput.class);

                    log.info("End createRoom output: {}", output);
                    return output;
                })
                .toEither()
                .mapLeft(throwable -> Match(throwable).of(
                        Case($(instanceOf(NotFoundException.class)), ex -> errorMapper.handleError(ex, HttpStatus.NOT_FOUND)),
                        Case($(instanceOf(NotAvailableException.class)), ex -> errorMapper.handleError(ex, HttpStatus.CONFLICT)),
                        Case($(), ex -> errorMapper.handleError(ex, HttpStatus.BAD_REQUEST))
                ));
    }

    private void checkIfRoomExists(CreateRoomInput input){
        log.info("Check if room with roomNumber {} already exists", input.getRoomNumber());
        if (roomRepository.existsByRoomNumber(input.getRoomNumber())){
            throw new NotAvailableException(String.format("Room with room number %s already exists", input.getRoomNumber()));
        }
        log.info("Room does not exist");
    }

    private List<BedSize> getBedSizes(CreateRoomInput input) {
        log.info("Get bed sizes from strings");
        List<BedSize> bedSizes = new ArrayList<>();
        if (input.getBedSizes() != null
                && !ObjectUtils.isEmpty(input.getBedSizes())) {
            bedSizes = input.getBedSizes()
                    .stream()
                    .map(BedSize::getByCode)
                    .toList();
        }
        log.info("Got all bed sizes");
        return bedSizes;
    }
}
