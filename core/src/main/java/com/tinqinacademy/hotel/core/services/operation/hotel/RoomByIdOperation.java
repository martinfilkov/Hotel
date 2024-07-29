package com.tinqinacademy.hotel.core.services.operation.hotel;

import com.tinqinacademy.hotel.api.operations.exception.NotFoundException;
import com.tinqinacademy.hotel.api.operations.hotel.roombyid.RoomByIdInput;
import com.tinqinacademy.hotel.api.operations.hotel.roombyid.RoomByIdOutput;
import com.tinqinacademy.hotel.api.operations.hotel.roombyid.RoomByIdProcess;
import com.tinqinacademy.hotel.core.utils.DateUtils;
import com.tinqinacademy.hotel.persistence.entity.Reservation;
import com.tinqinacademy.hotel.persistence.entity.Room;
import com.tinqinacademy.hotel.persistence.repositories.ReservationRepository;
import com.tinqinacademy.hotel.persistence.repositories.RoomRepository;
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
public class RoomByIdOperation implements RoomByIdProcess {
    private final ReservationRepository reservationRepository;
    private final RoomRepository roomRepository;
    private final ConversionService conversionService;

    @Autowired
    public RoomByIdOperation(ReservationRepository reservationRepository, RoomRepository roomRepository, ConversionService conversionService) {
        this.reservationRepository = reservationRepository;
        this.roomRepository = roomRepository;
        this.conversionService = conversionService;
    }

    @Override
    public RoomByIdOutput process(RoomByIdInput input) {
        log.info("Start getRoom input: {}", input);

        Room room = getExistingRoom(input);

        List<Reservation> reservations = reservationRepository.findByRoomId(room.getId());
        List<LocalDate> datesOccupied = DateUtils.getDatesOccupied(reservations);

        RoomByIdOutput output = conversionService.convert(room, RoomByIdOutput.RoomByIdOutputBuilder.class)
                .datesOccupied(datesOccupied)
                .build();

        log.info("End getRoom output: {}", output);
        return output;
    }

    private Room getExistingRoom(RoomByIdInput input){
        log.info("Try to get a room with id: {}", input.getId());

        Optional<Room> roomOptional = roomRepository.findById(UUID.fromString((input.getId())));

        if (roomOptional.isEmpty()) {
            throw new NotFoundException("Room with id " + input.getId() + " not found");
        }

        log.info("Found a room with id: {}", input.getId());
        return roomOptional.get();
    }
}
