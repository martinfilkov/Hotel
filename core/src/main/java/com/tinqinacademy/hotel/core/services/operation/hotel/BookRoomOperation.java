package com.tinqinacademy.hotel.core.services.operation.hotel;

import com.tinqinacademy.hotel.api.operations.exception.InvalidInputException;
import com.tinqinacademy.hotel.api.operations.exception.NotAvailableException;
import com.tinqinacademy.hotel.api.operations.exception.NotFoundException;
import com.tinqinacademy.hotel.api.operations.hotel.bookroom.BookRoomInput;
import com.tinqinacademy.hotel.api.operations.hotel.bookroom.BookRoomOutput;
import com.tinqinacademy.hotel.api.operations.hotel.bookroom.BookRoomProcess;
import com.tinqinacademy.hotel.persistence.entity.Reservation;
import com.tinqinacademy.hotel.persistence.entity.Room;
import com.tinqinacademy.hotel.persistence.entity.User;
import com.tinqinacademy.hotel.persistence.repositories.ReservationRepository;
import com.tinqinacademy.hotel.persistence.repositories.RoomRepository;
import com.tinqinacademy.hotel.persistence.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class BookRoomOperation implements BookRoomProcess {
    private final ReservationRepository reservationRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final ConversionService conversionService;

    @Autowired
    public BookRoomOperation(ReservationRepository reservationRepository, RoomRepository roomRepository, UserRepository userRepository, ConversionService conversionService) {
        this.reservationRepository = reservationRepository;
        this.roomRepository = roomRepository;
        this.userRepository = userRepository;
        this.conversionService = conversionService;
    }

    @Override
    public BookRoomOutput process(final BookRoomInput input) {
        log.info("Start bookRoom input: {}", input);
        checkIfReservationPeriodIsValid(input);
        checkIfReservationExists(input);

        User user = getIfUserExists(input);
        Room room = getIfRoomExists(input);

        Reservation reservation = conversionService.convert(input, Reservation.ReservationBuilder.class)
                .room(room)
                .user(user)
                .build();

        reservationRepository.save(reservation);

        BookRoomOutput output = new BookRoomOutput();
        log.info("End bookRoom output: {}", output);
        return output;
    }

    private User getIfUserExists(BookRoomInput input){
        log.info("Try to get a user with user id: {}", input.getUserId());
        Optional<User> userOptional = userRepository.findById(UUID.fromString(input.getUserId()));
        if (userOptional.isEmpty()) {
            throw new NotFoundException(String.format("User with id %s not found", input.getUserId()));
        }

        log.info("Found user with user id: {}", input.getUserId());
        return userOptional.get();
    }

    private Room getIfRoomExists(BookRoomInput input){
        log.info("Try to get a room with room id: {}", input.getRoomId());

        Optional<Room> roomOptional = roomRepository.findById(UUID.fromString(input.getRoomId()));
        if (roomOptional.isEmpty()) {
            throw new NotFoundException(String.format("Room with id %s not found", input.getRoomId()));
        }

        log.info("Found a room with room id: {}", input.getRoomId());
        return roomOptional.get();
    }

    private void checkIfReservationExists(BookRoomInput input){
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

    private void checkIfReservationPeriodIsValid(BookRoomInput input){
        log.info("Check if period is valid");
        if (input.getEndDate().isBefore(input.getStartDate())) {
            throw new InvalidInputException("Start date cannot be after end date");
        }
        log.info("Period is valid");
    }
}
