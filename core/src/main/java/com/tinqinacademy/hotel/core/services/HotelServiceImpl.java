package com.tinqinacademy.hotel.core.services;

import com.tinqinacademy.hotel.api.operations.exception.InvalidInputException;
import com.tinqinacademy.hotel.api.operations.exception.NotAvailableException;
import com.tinqinacademy.hotel.api.operations.exception.NotFoundException;
import com.tinqinacademy.hotel.api.operations.hotel.bookroom.BookRoomInput;
import com.tinqinacademy.hotel.api.operations.hotel.bookroom.BookRoomOutput;
import com.tinqinacademy.hotel.api.operations.hotel.getroomids.GetRoomIdsInput;
import com.tinqinacademy.hotel.api.operations.hotel.getroomids.GetRoomIdsOutput;
import com.tinqinacademy.hotel.api.operations.hotel.roombyid.RoomByIdInput;
import com.tinqinacademy.hotel.api.operations.hotel.roombyid.RoomByIdOutput;
import com.tinqinacademy.hotel.api.operations.hotel.unbookroom.UnbookRoomInput;
import com.tinqinacademy.hotel.api.operations.hotel.unbookroom.UnbookRoomOutput;
import com.tinqinacademy.hotel.core.utils.DateUtils;
import com.tinqinacademy.hotel.persistence.entity.Reservation;
import com.tinqinacademy.hotel.persistence.entity.Room;
import com.tinqinacademy.hotel.persistence.entity.User;
import com.tinqinacademy.hotel.persistence.model.BathroomType;
import com.tinqinacademy.hotel.persistence.repositories.ReservationRepository;
import com.tinqinacademy.hotel.persistence.repositories.RoomRepository;
import com.tinqinacademy.hotel.persistence.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class HotelServiceImpl implements HotelService {
    private final RoomRepository roomRepository;
    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final ConversionService conversionService;

    @Autowired
    public HotelServiceImpl(RoomRepository roomRepository, ReservationRepository reservationRepository, UserRepository userRepository, ConversionService conversionService) {
        this.roomRepository = roomRepository;
        this.reservationRepository = reservationRepository;
        this.userRepository = userRepository;
        this.conversionService = conversionService;
    }

    @Override
    public GetRoomIdsOutput getRoomIds(GetRoomIdsInput input) {
        log.info("Start getRoomIds input: {}", input);
        List<Room> rooms = roomRepository.findAvailableRooms(input.getStartDate(), input.getEndDate());

        List<Room> availableRooms = rooms.stream()
                .filter(room -> input.getBedSize()
                        .map(size -> room.getBedSizes().stream()
                                .anyMatch(bed -> bed.getBedSize().toString().equals(size)))
                        .orElse(true))
                .filter(room ->
                        input.getBathroomType()
                                .map(type -> BathroomType.getByCode(type) == room.getBathroomType())
                                .orElse(true)
                )
                .toList();

        GetRoomIdsOutput output = conversionService.convert(availableRooms, GetRoomIdsOutput.class);

        log.info("End getRoomIds output: {}", output);
        return output;
    }

    @Override
    public RoomByIdOutput getRoom(RoomByIdInput input) {
        log.info("Start getRoom input: {}", input);

        Optional<Room> roomOptional = roomRepository.findById(UUID.fromString((input.getId())));

        if (roomOptional.isEmpty()) {
            throw new NotFoundException("Room with id " + input.getId() + " not found");
        }

        Room room = roomOptional.get();

        List<Reservation> reservations = reservationRepository.findByRoomId(room.getId());
        List<LocalDate> datesOccupied = DateUtils.getDatesOccupied(reservations);

        RoomByIdOutput output = conversionService.convert(room, RoomByIdOutput.RoomByIdOutputBuilder.class)
                .datesOccupied(datesOccupied)
                .build();

        log.info("End getRoom output: {}", output);
        return output;
    }

    @Override
    public BookRoomOutput bookRoom(final BookRoomInput input) {
        log.info("Start bookRoom input: {}", input);

        Optional<User> userOptional = userRepository.findById(UUID.fromString(input.getUserId()));
        if (userOptional.isEmpty()) {
            throw new NotFoundException("User with id " + input.getUserId() + " not found");
        }

        if (input.getEndDate().isBefore(input.getStartDate())) {
            throw new InvalidInputException("Start date cannot be after end date");
        }

        Room room = checkIfRoomExists(input.getRoomId());

        checkIfReservationExists(input);

        Reservation reservation = conversionService.convert(input, Reservation.ReservationBuilder.class)
                .room(room)
                .user(userOptional.get())
                .build();

        reservationRepository.save(reservation);

        BookRoomOutput output = new BookRoomOutput();
        log.info("End bookRoom output: {}", output);
        return output;
    }

    private Room checkIfRoomExists(String roomId){
        Optional<Room> roomOptional = roomRepository.findById(UUID.fromString(roomId));
        if (roomOptional.isEmpty()) {
            throw new NotFoundException("Room with id %s not found");
        }
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

    @Override
    public UnbookRoomOutput unbookRoom(UnbookRoomInput input) {
        log.info("Start unbookRoom input: {}", input);
        Optional<Reservation> reservationOptional =
                reservationRepository.findById(UUID.fromString(input.getBookingId()));

        if (reservationOptional.isEmpty()) {
            throw new NotFoundException("Reservation with id " + input.getBookingId() + " does not exist");
        }

        reservationRepository.delete(reservationOptional.get());
        UnbookRoomOutput output = new UnbookRoomOutput();
        log.info("End unbookRoom output: {}", output);
        return output;
    }
}
