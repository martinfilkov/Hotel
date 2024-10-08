package com.tinqinacademy.hotel.core.services.operations.system;

import com.tinqinacademy.hotel.api.operations.base.Errors;
import com.tinqinacademy.hotel.api.operations.exceptions.NotFoundException;
import com.tinqinacademy.hotel.api.operations.system.deleteroom.DeleteRoomInput;
import com.tinqinacademy.hotel.api.operations.system.deleteroom.DeleteRoomOperation;
import com.tinqinacademy.hotel.api.operations.system.deleteroom.DeleteRoomOutput;
import com.tinqinacademy.hotel.core.services.operations.BaseOperationProcessor;
import com.tinqinacademy.hotel.core.utils.ErrorMapper;
import com.tinqinacademy.hotel.persistence.entities.Room;
import com.tinqinacademy.hotel.persistence.repositories.ReservationRepository;
import com.tinqinacademy.hotel.persistence.repositories.RoomRepository;
import io.vavr.control.Either;
import io.vavr.control.Try;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

import static io.vavr.API.*;
import static io.vavr.Predicates.instanceOf;

@Slf4j
@Service
public class DeleteRoomOperationProcessor extends BaseOperationProcessor implements DeleteRoomOperation {
    private final RoomRepository roomRepository;
    private final ReservationRepository reservationRepository;

    @Autowired
    public DeleteRoomOperationProcessor(RoomRepository roomRepository,
                                        ConversionService conversionService,
                                        Validator validator,
                                        ErrorMapper errorMapper,
                                        ReservationRepository reservationRepository) {
        super(conversionService, validator, errorMapper);
        this.roomRepository = roomRepository;
        this.reservationRepository = reservationRepository;
    }

    @Override
    public Either<Errors, DeleteRoomOutput> process(DeleteRoomInput input) {
        return validateInput(input)
                .flatMap(validated -> deleteRoom(input));
    }

    private Either<Errors, DeleteRoomOutput> deleteRoom(DeleteRoomInput input){
        return Try.of(() -> {
                    log.info("Start deleteRoom input: {}", input);

                    Room room = getIfRoomExists(input);
                    checkIfRoomHasReservations(room);

                    roomRepository.delete(room);

                    DeleteRoomOutput output = DeleteRoomOutput.builder().build();

                    log.info("End deleteRoom output: {}", output);
                    return output;
                })
                .toEither()
                .mapLeft(throwable -> Match(throwable).of(
                        Case($(instanceOf(NotFoundException.class)), ex -> errorMapper.handleError(ex, HttpStatus.NOT_FOUND)),
                        Case($(), ex -> errorMapper.handleError(ex, HttpStatus.BAD_REQUEST))
                ));
    }

    private Room getIfRoomExists(DeleteRoomInput input) {
        log.info("Try to get room with id: {}", input.getId());

        Optional<Room> roomOptional = roomRepository.findById(UUID.fromString(input.getId()));
        if (roomOptional.isEmpty()) {
            throw new NotFoundException("Room with id " + input.getId() + " not found");
        }

        log.info("Room with id {} exists", input.getId());
        return roomOptional.get();
    }

    private void checkIfRoomHasReservations(Room room) {
        log.info("Checking if room with id {} has reservations", room.getId());

        if (reservationRepository.existsByRoomId(room.getId())) {
            throw new IllegalStateException("Room with id " + room.getId() + " cannot be deleted as it has active reservations");
        }

        log.info("Room with id {} has no reservations", room.getId());
    }
}
