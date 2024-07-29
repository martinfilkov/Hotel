package com.tinqinacademy.hotel.core.services.operation.system;

import com.tinqinacademy.hotel.api.operations.exception.NotFoundException;
import com.tinqinacademy.hotel.api.operations.system.deleteroom.DeleteRoomInput;
import com.tinqinacademy.hotel.api.operations.system.deleteroom.DeleteRoomOutput;
import com.tinqinacademy.hotel.api.operations.system.deleteroom.DeleteRoomProcess;
import com.tinqinacademy.hotel.persistence.entity.Room;
import com.tinqinacademy.hotel.persistence.repositories.RoomRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class DeleteRoomOperation implements DeleteRoomProcess {
    private final RoomRepository roomRepository;

    @Autowired
    public DeleteRoomOperation(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    @Override
    public DeleteRoomOutput process(DeleteRoomInput input) {
        log.info("Start deleteRoom input: {}", input);

        Room room = getIfRoomExists(input);

        roomRepository.delete(room);

        DeleteRoomOutput output = new DeleteRoomOutput();

        log.info("End deleteRoom output: {}", output);
        return output;
    }

    private Room getIfRoomExists(DeleteRoomInput input){
        log.info("Try to get room with id: {}", input.getId());

        Optional<Room> roomOptional = roomRepository.findById(UUID.fromString(input.getId()));
        if (roomOptional.isEmpty()) {
            throw new NotFoundException("Room with id " + input.getId() + " not found");
        }

        log.info("Room with id {} exists", input.getId());
        return roomOptional.get();
    }
}
