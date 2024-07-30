package com.tinqinacademy.hotel.core.services.operation.system;

import com.tinqinacademy.hotel.api.operations.exception.InvalidInputException;
import com.tinqinacademy.hotel.api.operations.system.registervisitor.RegisterVisitorInputList;
import com.tinqinacademy.hotel.api.operations.system.registervisitor.RegisterVisitorOutput;
import com.tinqinacademy.hotel.api.operations.system.registervisitor.RegisterVisitorProcess;
import com.tinqinacademy.hotel.persistence.entity.Guest;
import com.tinqinacademy.hotel.persistence.entity.Reservation;
import com.tinqinacademy.hotel.persistence.repositories.GuestRepository;
import com.tinqinacademy.hotel.persistence.repositories.ReservationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class RegisterVisitorOperation implements RegisterVisitorProcess {
    private final ReservationRepository reservationRepository;
    private final GuestRepository guestRepository;
    private final ConversionService conversionService;

    @Autowired
    public RegisterVisitorOperation(ReservationRepository reservationRepository, GuestRepository guestRepository, ConversionService conversionService) {
        this.reservationRepository = reservationRepository;
        this.guestRepository = guestRepository;
        this.conversionService = conversionService;
    }

    @Override
    public RegisterVisitorOutput process(RegisterVisitorInputList inputList) {
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

        Optional<Reservation> reservationOptional = reservationRepository.findAvailableRoomByRoomNumberAndPeriod(
                inputList.getRoomNumber(),
                inputList.getStartDate(),
                inputList.getEndDate()
        );

        if (reservationOptional.isEmpty()) {
            throw new InvalidInputException(
                    "The room number you specified does not exist or is not available between the given period"
            );
        }

        log.info("Found reservation with room number: {}", inputList.getRoomNumber());
        return reservationOptional.get();
    }
}
