package com.tinqinacademy.hotel.core.services.operations.system;

import com.tinqinacademy.hotel.api.operations.base.Errors;
import com.tinqinacademy.hotel.api.operations.exceptions.InvalidInputException;
import com.tinqinacademy.hotel.api.operations.exceptions.NotAvailableException;
import com.tinqinacademy.hotel.api.operations.exceptions.NotFoundException;
import com.tinqinacademy.hotel.api.operations.system.registervisitor.RegisterVisitorInputList;
import com.tinqinacademy.hotel.api.operations.system.registervisitor.RegisterVisitorOperation;
import com.tinqinacademy.hotel.api.operations.system.registervisitor.RegisterVisitorOutput;
import com.tinqinacademy.hotel.core.services.operations.BaseOperationProcessor;
import com.tinqinacademy.hotel.core.utils.ErrorMapper;
import com.tinqinacademy.hotel.persistence.entities.Guest;
import com.tinqinacademy.hotel.persistence.entities.Reservation;
import com.tinqinacademy.hotel.persistence.repositories.GuestRepository;
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

import java.util.List;
import java.util.Optional;

import static io.vavr.API.*;
import static io.vavr.Predicates.instanceOf;

@Slf4j
@Service
public class RegisterVisitorOperationProcessor extends BaseOperationProcessor implements RegisterVisitorOperation {
    private final ReservationRepository reservationRepository;
    private final GuestRepository guestRepository;
    private final RoomRepository roomRepository;

    @Autowired
    public RegisterVisitorOperationProcessor(ReservationRepository reservationRepository,
                                             GuestRepository guestRepository,
                                             ConversionService conversionService,
                                             Validator validator,
                                             ErrorMapper errorMapper,
                                             RoomRepository roomRepository) {
        super(conversionService, validator, errorMapper);
        this.reservationRepository = reservationRepository;
        this.guestRepository = guestRepository;
        this.roomRepository = roomRepository;
    }

    @Override
    public Either<Errors, RegisterVisitorOutput> process(RegisterVisitorInputList inputList) {
        return validateInput(inputList)
                .flatMap(validated -> registerVisitor(inputList));
    }

    private Either<Errors, RegisterVisitorOutput> registerVisitor(RegisterVisitorInputList inputList) {
        return Try.of(() -> {
                    log.info("Start registerVisitor input: {}", inputList);

                    checkIfVisitorsNotEmpty(inputList);
                    checkIfReservationPeriodIsValid(inputList);

                    List<Guest> guestList = inputList
                            .getVisitors()
                            .stream()
                            .map(guest -> conversionService.convert(guest, Guest.class))
                            .toList();

                    Reservation reservation = getIfReservationExists(inputList);
                    List<Guest> allGuests = guestRepository.saveAll(guestList);
                    reservation.setGuests(allGuests);

                    reservationRepository.save(reservation);
                    RegisterVisitorOutput output = RegisterVisitorOutput.builder().build();

                    log.info("End registerVisitor output: {}", output);
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

    private void checkIfVisitorsNotEmpty(RegisterVisitorInputList inputList) {
        log.info("Check if visitors are empty");
        if (inputList.getVisitors().isEmpty()) {
            throw new InvalidInputException("Visitors is empty");
        }
        log.info("Visitors not empty");
    }

    private void checkIfReservationPeriodIsValid(RegisterVisitorInputList inputList) {
        log.info("Check if period is valid");
        if (inputList.getEndDate().isBefore(inputList.getStartDate())) {
            throw new InvalidInputException("Start date cannot be after end date");
        }
        log.info("Period is valid");
    }

    private Reservation getIfReservationExists(RegisterVisitorInputList inputList) {
        log.info("Try to get reservation with room number: {}", inputList.getRoomNumber());

        Boolean roomExists = roomRepository.existsByRoomNumber(inputList.getRoomNumber());
        if (!roomExists) {
            throw new NotFoundException(String.format("Room with room number %s not found", inputList.getRoomNumber()));
        }

        Optional<Reservation> reservationOptional = reservationRepository.findAvailableRoomByRoomNumberAndPeriod(
                inputList.getRoomNumber(),
                inputList.getStartDate(),
                inputList.getEndDate()
        );

        if (reservationOptional.isEmpty()) {
            throw new NotAvailableException("The room number you specified is not available between the given period");
        }

        if (!reservationOptional.get().getGuests().isEmpty()) {
            throw new NotAvailableException("Guest have already been registered with that reservation");
        }

        log.info("Found reservation with room number: {}", inputList.getRoomNumber());
        return reservationOptional.get();
    }
}
