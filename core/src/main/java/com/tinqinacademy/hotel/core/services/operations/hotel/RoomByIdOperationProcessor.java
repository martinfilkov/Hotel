package com.tinqinacademy.hotel.core.services.operations.hotel;

import com.tinqinacademy.hotel.api.operations.base.Errors;
import com.tinqinacademy.hotel.api.operations.exceptions.NotFoundException;
import com.tinqinacademy.hotel.api.operations.hotel.roombyid.RoomByIdInput;
import com.tinqinacademy.hotel.api.operations.hotel.roombyid.RoomByIdOperation;
import com.tinqinacademy.hotel.api.operations.hotel.roombyid.RoomByIdOutput;
import com.tinqinacademy.hotel.core.services.operations.BaseOperationProcessor;
import com.tinqinacademy.hotel.core.utils.DateUtils;
import com.tinqinacademy.hotel.core.utils.ErrorMapper;
import com.tinqinacademy.hotel.persistence.entities.Reservation;
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

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static io.vavr.API.*;
import static io.vavr.Predicates.instanceOf;

@Slf4j
@Service
public class RoomByIdOperationProcessor extends BaseOperationProcessor implements RoomByIdOperation {
    private final ReservationRepository reservationRepository;
    private final RoomRepository roomRepository;

    @Autowired
    public RoomByIdOperationProcessor(ReservationRepository reservationRepository,
                                      RoomRepository roomRepository,
                                      ConversionService conversionService,
                                      Validator validator,
                                      ErrorMapper errorMapper) {
        super(conversionService, validator, errorMapper);
        this.reservationRepository = reservationRepository;
        this.roomRepository = roomRepository;
    }

    @Override
    public Either<Errors, RoomByIdOutput> process(RoomByIdInput input) {
        return validateInput(input)
                .flatMap(validated -> roomById(input));
    }

    private Either<Errors, RoomByIdOutput> roomById(RoomByIdInput input) {
        return Try.of(() -> {
                    log.info("Start getRoom input: {}", input);
                    Room room = getExistingRoom(input);

                    List<Reservation> reservations = reservationRepository.findByRoomId(room.getId());
                    List<LocalDate> datesOccupied = DateUtils.getDatesOccupied(reservations);

                    RoomByIdOutput output = conversionService.convert(room, RoomByIdOutput.RoomByIdOutputBuilder.class)
                            .datesOccupied(datesOccupied)
                            .build();

                    log.info("End getRoom output: {}", output);
                    return output;
                })
                .toEither()
                .mapLeft(throwable -> Match(throwable).of(
                        Case($(instanceOf(NotFoundException.class)), ex -> errorMapper.handleError(ex, HttpStatus.NOT_FOUND)),
                        Case($(), ex -> errorMapper.handleError(ex, HttpStatus.BAD_REQUEST))
                ));
    }

    private Room getExistingRoom(RoomByIdInput input) {
        log.info("Try to get a room with id: {}", input.getId());

        Optional<Room> roomOptional = roomRepository.findById(UUID.fromString((input.getId())));

        if (roomOptional.isEmpty()) {
            throw new NotFoundException(String.format("Room with id %s not found", input.getId()));
        }

        log.info("Found a room with id: {}", input.getId());
        return roomOptional.get();
    }
}
