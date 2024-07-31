package com.tinqinacademy.hotel.core.services.operation.hotel;

import com.tinqinacademy.hotel.api.operations.base.Errors;
import com.tinqinacademy.hotel.api.operations.exception.NotFoundException;
import com.tinqinacademy.hotel.api.operations.hotel.unbookroom.UnbookRoomInput;
import com.tinqinacademy.hotel.api.operations.hotel.unbookroom.UnbookRoomOperation;
import com.tinqinacademy.hotel.api.operations.hotel.unbookroom.UnbookRoomOutput;
import com.tinqinacademy.hotel.core.services.operation.BaseOperationProcessor;
import com.tinqinacademy.hotel.core.utils.ErrorMapper;
import com.tinqinacademy.hotel.persistence.entity.Reservation;
import com.tinqinacademy.hotel.persistence.repositories.ReservationRepository;
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
public class UnbookRoomOperationProcessor extends BaseOperationProcessor implements UnbookRoomOperation {
    private final ReservationRepository reservationRepository;

    @Autowired
    public UnbookRoomOperationProcessor(ReservationRepository reservationRepository,
                                        ConversionService conversionService,
                                        Validator validator,
                                        ErrorMapper errorMapper) {
        super(conversionService, validator, errorMapper);
        this.reservationRepository = reservationRepository;
    }

    @Override
    public Either<Errors, UnbookRoomOutput> process(UnbookRoomInput input) {
        return validateInput(input)
                .flatMap(this::unbookRoom);
    }

    private Either<Errors, UnbookRoomOutput> unbookRoom(UnbookRoomInput input){
        return Try.of(() -> {
                    log.info("Start unbookRoom input: {}", input);
                    Reservation reservation = getIfReservationExists(input);
                    reservationRepository.delete(reservation);
                    UnbookRoomOutput output = new UnbookRoomOutput();
                    log.info("End unbookRoom output: {}", output);
                    return output;
                })
                .toEither()
                .mapLeft(throwable -> Match(throwable).of(
                        Case($(instanceOf(NotFoundException.class)), ex -> errorMapper.handleError(ex, HttpStatus.NOT_FOUND)),
                        Case($(), ex -> errorMapper.handleError(ex, HttpStatus.BAD_REQUEST))
                ));
    }

    private Reservation getIfReservationExists(UnbookRoomInput input) {
        log.info("Try to get reservation with id: {}", input.getBookingId());
        Optional<Reservation> reservationOptional =
                reservationRepository.findById(UUID.fromString(input.getBookingId()));

        if (reservationOptional.isEmpty()) {
            throw new NotFoundException("Reservation with id " + input.getBookingId() + " does not exist");
        }

        log.info("Found reservation with id: {}", input.getBookingId());
        return reservationOptional.get();
    }
}
