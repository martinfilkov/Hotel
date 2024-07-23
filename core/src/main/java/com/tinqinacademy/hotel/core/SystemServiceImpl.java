package com.tinqinacademy.hotel.core;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
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
import com.tinqinacademy.hotel.persistence.repositories.BedRepository;
import com.tinqinacademy.hotel.persistence.repositories.RoomRepository;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class SystemServiceImpl implements SystemService {
    private final RoomRepository roomRepository;
    private final BedRepository bedRepository;
    private final ObjectMapper mapper;

    @Autowired
    public SystemServiceImpl(RoomRepository roomRepository, BedRepository bedRepository, ObjectMapper mapper) {
        this.roomRepository = roomRepository;
        this.bedRepository = bedRepository;
        this.mapper = mapper;
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

        if (BathroomType.getByCode(input.getBathRoomType()).equals(BathroomType.UNKNOWN)) {
            throw new NotFoundException("Bathroom type " + input.getBathRoomType() + " not found");
        }

        input.getBedSizes().forEach(bedSize ->
        {
            if (BedSize.getByCode(bedSize).equals(BedSize.UNKNOWN)) {
                throw new NotFoundException("Bed size " + bedSize + " not found");
            }
        });

        Room room = Room.builder()
                .bathroomType(BathroomType.getByCode(input.getBathRoomType()))
                .floor(input.getFloor())
                .roomNumber(input.getRoomNumber())
                .price(input.getPrice())
                .bedSizes(input.getBedSizes().stream().map(bed ->
                                bedRepository.findByBedSize(BedSize.getByCode(bed)).orElseThrow())
                        .toList()
                )
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

        Optional<Room> roomOptional = roomRepository.findById(UUID.fromString(input.getRoomId()));
        if (roomOptional.isEmpty()) {
            throw new NotFoundException("Room with id " + input.getRoomId() + " not found");
        }

        if (BathroomType.getByCode(input.getBathRoomType()).equals(BathroomType.UNKNOWN)) {
            throw new NotFoundException("Bathroom type " + input.getBathRoomType() + " not found");
        }

        input.getBedSizes().forEach(bedSize ->
        {
            if (BedSize.getByCode(bedSize).equals(BedSize.UNKNOWN)) {
                throw new NotFoundException("Bed size " + bedSize + " not found");
            }
        });

        Room room = Room.builder()
                .id(UUID.fromString(input.getRoomId()))
                .bedSizes(input.getBedSizes().stream().map(bed ->
                                bedRepository.findByBedSize(BedSize.getByCode(bed)).orElseThrow())
                        .toList()
                )
                .bathroomType(BathroomType.getByCode(input.getBathRoomType()))
                .roomNumber(input.getRoomNumber())
                .price(input.getPrice())
                .floor(roomOptional.get().getFloor())
                .build();

        Room updatedRoom = roomRepository.save(room);

        UpdateRoomOutput output = UpdateRoomOutput.builder()
                .id(updatedRoom.getId().toString())
                .build();

        log.info("End updateRoom output: {}", output);
        return output;
    }

    @SneakyThrows
    @Override
    public PartialUpdateRoomOutput partialUpdateRoom(PartialUpdateRoomInput input) {
        log.info("Start partialUpdateRoom input: {}", input);

        Optional<Room> roomOptional = roomRepository.findById(UUID.fromString(input.getRoomId()));
        if (roomOptional.isEmpty()) {
            throw new NotFoundException("Room with id " + input.getRoomId() + " not found");
        }

        if (BathroomType.getByCode(input.getBathRoomType()).equals(BathroomType.UNKNOWN)
                && input.getBathRoomType() != null) {
            throw new NotFoundException("Bathroom type " + input.getBathRoomType() + " not found");
        }

        if (input.getBedSizes() != null) {
            input.getBedSizes().forEach(bedSize ->
            {
                if (BedSize.getByCode(bedSize).equals(BedSize.UNKNOWN)) {
                    throw new NotFoundException("Bed size " + bedSize + " not found");
                }
            });
        }
        Room currentRoom = roomOptional.get();

        Room inputRoom = Room.builder()
                .price(input.getPrice())
                .roomNumber(input.getRoomNumber())
                .bedSizes(input.getBedSizes() != null ?
                        input.getBedSizes().stream().map(bed ->
                                bedRepository.findByBedSize(BedSize.getByCode(bed)).orElseThrow()
                        ).toList() : null)
                .bathroomType(!BathroomType.getByCode(input.getBathRoomType()).equals(BathroomType.UNKNOWN) ?
                        BathroomType.getByCode(input.getBathRoomType()) : null)
                .build();

        JsonNode roomNode = mapper.valueToTree(currentRoom);
        JsonNode inputNode = mapper.valueToTree(inputRoom);

        JsonMergePatch patch = JsonMergePatch.fromJson(inputNode);
        Room updatedRoom = mapper.treeToValue(patch.apply(roomNode), Room.class);

        roomRepository.save(updatedRoom);

        PartialUpdateRoomOutput output = PartialUpdateRoomOutput.builder()
                .id(updatedRoom.getId().toString())
                .build();

        log.info("End partialUpdateRoom output: {}", output);
        return output;
    }

    @Override
    public DeleteRoomOutput deleteRoom(DeleteRoomInput input) {
        log.info("Start deleteRoom input: {}", input);

        Optional<Room> roomOptional = roomRepository.findById(UUID.fromString(input.getId()));

        if (roomOptional.isEmpty()) {
            throw new NotFoundException("Room with id " + input.getId() + " not found");
        }

        roomRepository.delete(roomOptional.get());

        DeleteRoomOutput output = new DeleteRoomOutput();

        log.info("End deleteRoom output: {}", output);
        return output;
    }
}
