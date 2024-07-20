package com.tinqinacademy.hotel.core;

import com.tinqinacademy.hotel.api.operations.exception.NotFoundException;
import com.tinqinacademy.hotel.api.operations.system.createroom.CreateRoomInput;
import com.tinqinacademy.hotel.api.operations.system.createroom.CreateRoomOutput;
import com.tinqinacademy.hotel.api.operations.system.deleteroom.DeleteRoomInput;
import com.tinqinacademy.hotel.api.operations.system.deleteroom.DeleteRoomOutput;
import com.tinqinacademy.hotel.api.operations.system.inforregister.InfoRegisterInput;
import com.tinqinacademy.hotel.api.operations.system.inforregister.InfoRegisterOutput;
import com.tinqinacademy.hotel.api.operations.system.partialupdate.PartialUpdateRoomInput;
import com.tinqinacademy.hotel.api.operations.system.partialupdate.PartialUpdateRoomOutput;
import com.tinqinacademy.hotel.api.operations.system.registervisitor.RegisterVisitorInput;
import com.tinqinacademy.hotel.api.operations.system.registervisitor.RegisterVisitorOutput;
import com.tinqinacademy.hotel.api.operations.system.updateroom.UpdateRoomInput;
import com.tinqinacademy.hotel.api.operations.system.updateroom.UpdateRoomOutput;
import com.tinqinacademy.hotel.persistence.entity.Room;
import com.tinqinacademy.hotel.persistence.model.BathroomType;
import com.tinqinacademy.hotel.persistence.model.BedSize;
import com.tinqinacademy.hotel.persistence.repositories.RoomRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
public class SystemServiceImpl implements SystemService{
    private final RoomRepository roomRepository;

    @Autowired
    public SystemServiceImpl(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    @Override
    public RegisterVisitorOutput registerVisitor(RegisterVisitorInput input) {
        log.info("Start registerVisitor input: {}", input);

        RegisterVisitorOutput output = new RegisterVisitorOutput();

        log.info("End registerVisitor output: {}", output);
        return output;
    }

    @Override
    public InfoRegisterOutput getRegisterInfo(InfoRegisterInput input) {
        log.info("Start getRegisterInfo input: {}", input);

        InfoRegisterOutput output = InfoRegisterOutput.builder()
                .startDate(input.getStartDate())
                .endDate(input.getEndDate())
                .firstName(input.getFirstName())
                .lastName(input.getLastName())
                .phone(input.getPhone())
                .idCardNumber(input.getIdCardNumber())
                .idCardIssueAuthority(input.getIdCardIssueAuthority())
                .idCardValidity(input.getIdCardValidity())
                .idCardIssueDate(input.getIdCardIssueDate())
                .build();

        log.info("End getRegisterInfo output: {}", output);
        return output;
    }

    @Override
    public CreateRoomOutput createRoom(CreateRoomInput input) {
        log.info("Start createRoom input: {}", input);

        Room room = Room.builder()
                .bathroomType(BathroomType.getByCode(input.getBathRoomType()))
                .floor(input.getFloor())
                .roomNumber(input.getRoomNumber())
                .price(input.getPrice())
                .bedSizes(input.getBedSizes().stream().map(BedSize::getByCode).toList())
                .build();

        Room savedRoom = roomRepository.save(room);

        CreateRoomOutput output = CreateRoomOutput.builder()
                .id(savedRoom.getId().toString())
                .build();

        log.info("End createRoom output: {}", output);
        return output;
    }

    @Override
    public UpdateRoomOutput updateRoom(UpdateRoomInput input) {
        log.info("Start updateRoom input: {}", input);

        if(input.getRoomId().equals("5")) throw new NotFoundException("Room Id not found");

        UpdateRoomOutput output = UpdateRoomOutput.builder()
                .id(input.getRoomId())
                .build();

        log.info("End updateRoom output: {}", output);
        return output;
    }

    @Override
    public PartialUpdateRoomOutput partialUpdateRoom(PartialUpdateRoomInput input) {
        log.info("Start partialUpdateRoom input: {}", input);

        PartialUpdateRoomOutput output = PartialUpdateRoomOutput.builder()
                .id(input.getRoomId())
                .build();

        log.info("End partialUpdateRoom output: {}", output);
        return output;
    }

    @Override
    public DeleteRoomOutput deleteRoom(DeleteRoomInput input) {
        log.info("Start deleteRoom input: {}", input);

        roomRepository.delete(UUID.fromString(input.getId()));

        DeleteRoomOutput output = new DeleteRoomOutput();

        log.info("End deleteRoom output: {}", output);
        return output;
    }
}
