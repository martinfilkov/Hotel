package com.tinqinacademy.hotel.core;

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
import com.tinqinacademy.hotel.persistence.entity.Reservation;
import com.tinqinacademy.hotel.persistence.entity.Room;
import com.tinqinacademy.hotel.persistence.repositories.ReservationRepository;
import com.tinqinacademy.hotel.persistence.repositories.RoomRepository;
import com.tinqinacademy.hotel.persistence.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class HotelServiceImpl implements HotelService {
    private final RoomRepository roomRepository;
    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;

    @Autowired
    public HotelServiceImpl(RoomRepository roomRepository, ReservationRepository reservationRepository, UserRepository userRepository) {
        this.roomRepository = roomRepository;
        this.reservationRepository = reservationRepository;
        this.userRepository = userRepository;
    }

    @Override
    public GetRoomIdsOutput getRoomIds(GetRoomIdsInput input) {
        log.info("Start getRoomIds input: {}", input);
        GetRoomIdsOutput output = GetRoomIdsOutput.builder()
                .ids(List.of("5", "7", "9"))
                .build();

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
        RoomByIdOutput output = RoomByIdOutput.builder()
                .id(room.getId().toString())
                .floor(room.getFloor())
                .bathroomType(room.getBathroomType())
                .bedSizes(room.getBedSizes())
                .datesOccupied(new ArrayList<>())
                .price(room.getPrice())
                .build();

        log.info("End getRoom output: {}", output);
        return output;
    }

    @Override
    public BookRoomOutput bookRoom(final BookRoomInput input) {
        log.info("Start bookRoom input: {}", input);

        if(roomRepository.findById(UUID.fromString(input.getRoomId())).isEmpty()){
            throw new NotFoundException("Room with id " + input.getRoomId() + " not found");
        }

        if(userRepository.findById(UUID.fromString(input.getUserId())).isEmpty()){
            throw new NotFoundException("User with id " + input.getUserId() + " not found");
        }

        if(reservationRepository.existsByRoomIdAndDateRange(
                UUID.fromString(input.getRoomId()),
                input.getStartDate(),
                input.getEndDate())){
            throw new NotAvailableException("Room with id " + input.getRoomId() + " is not available");
        }

        Reservation reservation = Reservation.builder()
                .startDate(input.getStartDate())
                .endDate(input.getEndDate())
                .roomId(UUID.fromString(input.getRoomId()))
                .userId(UUID.fromString(input.getUserId()))
                .build();

        reservationRepository.save(reservation);

        BookRoomOutput output = new BookRoomOutput();
        log.info("End bookRoom output: {}", output);
        return output;
    }

    @Override
    public UnbookRoomOutput unbookRoom(UnbookRoomInput input) {
        log.info("Start unbookRoom input: {}", input);
        reservationRepository.delete(UUID.fromString(input.getBookingId()));
        UnbookRoomOutput output = new UnbookRoomOutput();
        log.info("End unbookRoom output: {}", output);
        return output;
    }
}
