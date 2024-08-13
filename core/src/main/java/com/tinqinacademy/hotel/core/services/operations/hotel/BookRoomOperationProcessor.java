package com.tinqinacademy.hotel.core.services.operations.hotel;

import com.tinqinacademy.hotel.api.operations.base.Errors;
import com.tinqinacademy.hotel.api.operations.exceptions.InvalidInputException;
import com.tinqinacademy.hotel.api.operations.exceptions.NotAvailableException;
import com.tinqinacademy.hotel.api.operations.exceptions.NotFoundException;
import com.tinqinacademy.hotel.api.operations.hotel.bookroom.BookRoomInput;
import com.tinqinacademy.hotel.api.operations.hotel.bookroom.BookRoomOperation;
import com.tinqinacademy.hotel.api.operations.hotel.bookroom.BookRoomOutput;
import com.tinqinacademy.hotel.core.services.operations.BaseOperationProcessor;
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

import java.util.Optional;
import java.util.UUID;

import static io.vavr.API.*;
import static io.vavr.Predicates.instanceOf;

@Slf4j
@Service
public class BookRoomOperationProcessor extends BaseOperationProcessor implements BookRoomOperation {
    private final ReservationRepository reservationRepository;
    private final RoomRepository roomRepository;

    @Autowired
    public BookRoomOperationProcessor(ReservationRepository reservationRepository,
                                      RoomRepository roomRepository,
                                      ConversionService conversionService,
                                      Validator validator,
                                      ErrorMapper errorMapper) {
        super(conversionService, validator, errorMapper);
        this.reservationRepository = reservationRepository;
        this.roomRepository = roomRepository;
    }

    @Override
    public Either<Errors, BookRoomOutput> process(final BookRoomInput input) {
        return validateInput(input)
                .flatMap(validated -> bookRoom(input));
    }

    private Either<Errors, BookRoomOutput> bookRoom(BookRoomInput input){
        return Try.of(() -> {
                    log.info("Start bookRoom input: {}", input);
                    checkIfReservationPeriodIsValid(input);
                    checkIfReservationExists(input);

                    Room room = getIfRoomExists(input);

                    Reservation reservation = conversionService.convert(input, Reservation.ReservationBuilder.class)
                            .room(room)
                            .userId(UUID.fromString(input.getUserId()))
                            .build();

                    reservationRepository.save(reservation);

                    BookRoomOutput output = BookRoomOutput.builder().build();
                    log.info("End bookRoom output: {}", output);
                    return output;
                })
                .toEither()
                .mapLeft(throwable -> Match(throwable).of(
                        Case($(instanceOf(NotFoundException.class)), ex -> errorMapper.handleError(ex, HttpStatus.NOT_FOUND)),
                        Case($(instanceOf(NotAvailableException.class)), ex -> errorMapper.handleError(ex, HttpStatus.CONFLICT)),
                        Case($(instanceOf(InvalidInputException.class)), ex -> errorMapper.handleError(ex, HttpStatus.BAD_REQUEST)),
                        Case($(), ex -> errorMapper.handleError(ex, HttpStatus.BAD_REQUEST))
                ));
    }

    private Room getIfRoomExists(BookRoomInput input) {
        log.info("Try to get a room with room id: {}", input.getRoomId());

        Optional<Room> roomOptional = roomRepository.findById(UUID.fromString(input.getRoomId()));
        if (roomOptional.isEmpty()) {
            throw new NotFoundException(String.format("Room with id %s not found", input.getRoomId()));
        }

        log.info("Found a room with room id: {}", input.getRoomId());
        return roomOptional.get();
    }

    private void checkIfReservationExists(BookRoomInput input) {
        log.info("Start checkIfReservationExists with input: {}", input);
        boolean checkIfReservationExists = reservationRepository.existsByRoomIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                UUID.fromString(input.getRoomId()),
                input.getEndDate(),
                input.getStartDate());

        if (checkIfReservationExists) {
            throw new NotAvailableException(String.format("Room with id %s is not available within the given period", input.getRoomId()));
        }
        log.info("End checkIfReservationExists no such room exists");
    }

    private void checkIfReservationPeriodIsValid(BookRoomInput input) {
        log.info("Check if period is valid");
        if (input.getEndDate().isBefore(input.getStartDate())) {
            throw new InvalidInputException("Start date cannot be after end date");
        }
        log.info("Period is valid");
    }
}
