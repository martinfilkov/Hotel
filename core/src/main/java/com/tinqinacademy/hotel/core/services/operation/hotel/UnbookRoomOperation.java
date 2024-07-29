package com.tinqinacademy.hotel.core.services.operation.hotel;

import com.tinqinacademy.hotel.api.operations.exception.NotFoundException;
import com.tinqinacademy.hotel.api.operations.hotel.unbookroom.UnbookRoomInput;
import com.tinqinacademy.hotel.api.operations.hotel.unbookroom.UnbookRoomOutput;
import com.tinqinacademy.hotel.api.operations.hotel.unbookroom.UnbookRoomProcess;
import com.tinqinacademy.hotel.persistence.entity.Reservation;
import com.tinqinacademy.hotel.persistence.repositories.ReservationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class UnbookRoomOperation implements UnbookRoomProcess {
    private final ReservationRepository reservationRepository;

    @Autowired
    public UnbookRoomOperation(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    @Override
    public UnbookRoomOutput process(UnbookRoomInput input) {
        log.info("Start unbookRoom input: {}", input);
        Reservation reservation = getIfReservationExists(input);
        reservationRepository.delete(reservation);
        UnbookRoomOutput output = new UnbookRoomOutput();
        log.info("End unbookRoom output: {}", output);
        return output;
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
