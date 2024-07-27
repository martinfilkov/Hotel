package com.tinqinacademy.hotel.core.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import com.tinqinacademy.hotel.api.operations.exception.InvalidInputException;
import com.tinqinacademy.hotel.api.operations.exception.NotFoundException;
import com.tinqinacademy.hotel.api.operations.system.createroom.CreateRoomInput;
import com.tinqinacademy.hotel.api.operations.system.createroom.CreateRoomOutput;
import com.tinqinacademy.hotel.api.operations.system.deleteroom.DeleteRoomInput;
import com.tinqinacademy.hotel.api.operations.system.deleteroom.DeleteRoomOutput;
import com.tinqinacademy.hotel.api.operations.system.inforregister.InfoRegisterInput;
import com.tinqinacademy.hotel.api.operations.system.inforregister.InfoRegisterOutput;
import com.tinqinacademy.hotel.api.operations.system.inforregister.InfoRegisterOutputList;
import com.tinqinacademy.hotel.api.operations.system.partialupdate.PartialUpdateRoomInput;
import com.tinqinacademy.hotel.api.operations.system.partialupdate.PartialUpdateRoomOutput;
import com.tinqinacademy.hotel.api.operations.system.registervisitor.RegisterVisitorInputList;
import com.tinqinacademy.hotel.api.operations.system.registervisitor.RegisterVisitorOutput;
import com.tinqinacademy.hotel.api.operations.system.updateroom.UpdateRoomInput;
import com.tinqinacademy.hotel.api.operations.system.updateroom.UpdateRoomOutput;
import com.tinqinacademy.hotel.core.converters.GuestToInfoRegisterOutputConverter;
import com.tinqinacademy.hotel.core.utils.SpecificationUtils;
import com.tinqinacademy.hotel.persistence.entity.Guest;
import com.tinqinacademy.hotel.persistence.entity.Reservation;
import com.tinqinacademy.hotel.persistence.entity.Room;
import com.tinqinacademy.hotel.persistence.model.BathroomType;
import com.tinqinacademy.hotel.persistence.model.BedSize;
import com.tinqinacademy.hotel.persistence.repositories.BedRepository;
import com.tinqinacademy.hotel.persistence.repositories.GuestRepository;
import com.tinqinacademy.hotel.persistence.repositories.ReservationRepository;
import com.tinqinacademy.hotel.persistence.repositories.RoomRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.tinqinacademy.hotel.core.specifications.GuestSpecifications.*;

@Slf4j
@Service
public class SystemServiceImpl implements SystemService {
    private final RoomRepository roomRepository;
    private final BedRepository bedRepository;
    private final ReservationRepository reservationRepository;
    private final GuestRepository guestRepository;
    private final ConversionService conversionService;
    private final ObjectMapper mapper;
    private final EntityManager entityManager;

    @Autowired
    public SystemServiceImpl(RoomRepository roomRepository, BedRepository bedRepository, GuestRepository guestRepository, ReservationRepository reservationRepository, ConversionService conversionService, ObjectMapper mapper, EntityManager entityManager) {
        this.roomRepository = roomRepository;
        this.bedRepository = bedRepository;
        this.reservationRepository = reservationRepository;
        this.guestRepository = guestRepository;
        this.conversionService = conversionService;
        this.mapper = mapper;
        this.entityManager = entityManager;
    }

    @Override
    public RegisterVisitorOutput registerVisitor(RegisterVisitorInputList inputList) {
        log.info("Start registerVisitor input: {}", inputList);

        if (inputList.getVisitors().isEmpty()) {
            throw new InvalidInputException("Visitors is empty");
        }

        if (inputList.getEndDate().isBefore(inputList.getStartDate())) {
            throw new InvalidInputException("Start date cannot be after end date");
        }

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

        List<Guest> guestList = inputList.getVisitors().stream()
                .map(guest -> conversionService.convert(guest, Guest.class))
                .toList();

        List<Guest> allGuests = guestRepository.saveAll(guestList);

        Reservation reservation = reservationOptional.get();
        reservation.setGuests(allGuests);

        reservationRepository.save(reservation);
        RegisterVisitorOutput output = new RegisterVisitorOutput();

        log.info("End registerVisitor output: {}", output);
        return output;
    }

    @Override
    public InfoRegisterOutputList getRegisterInfo(InfoRegisterInput input) {
        log.info("Start getRegisterInfo input: {}", input);

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        List<Specification<Guest>> predicates = new ArrayList<>() {{
            add(guestHasFirstName(input.getFirstName()));
            add(guestHasLastName(input.getLastName()));
            add(guestHasPhoneNumber(input.getPhone()));
            add(guestHasIdCardNumber(input.getIdCardNumber()));
            add(guestHasIdCardValidity(input.getIdCardValidity()));
            add(guestHasIdCardIssueDate(input.getIdCardIssueDate()));
            add(guestHasIdCardIssueAuthority(input.getIdCardIssueAuthority()));
        }};

        Specification<Guest> specification = SpecificationUtils.combineSpecifications(predicates);
        List<Guest> specifiedGuests = guestRepository.findAll(specification);
        List<Guest> allGuests = guestRepository.findByDateRangeAndRoomNumber(
                input.getStartDate(), input.getEndDate(), input.getRoomNumber()
        );

        List<Guest> filteredGuests = allGuests.stream()
                .filter(specifiedGuests::contains)
                .toList();

        List<InfoRegisterOutput> guestInfo = filteredGuests.stream()
                .map(guest -> conversionService.convert(guest, InfoRegisterOutput.class))
                .toList();

        InfoRegisterOutputList output = InfoRegisterOutputList.builder()
                .visitors(guestInfo)
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

        Room room = conversionService.convert(input, Room.RoomBuilder.class)
                .bedSizes(input.getBedSizes().stream().map(bed ->
                                bedRepository.findByBedSize(BedSize.getByCode(bed)).orElseThrow())
                        .toList()
                )
                .build();

        Room savedRoom = roomRepository.save(room);

        CreateRoomOutput output = conversionService.convert(savedRoom, CreateRoomOutput.class);

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

        Room room = conversionService.convert(input, Room.RoomBuilder.class)
                .bedSizes(input.getBedSizes().stream().map(bed ->
                                bedRepository.findByBedSize(BedSize.getByCode(bed)).orElseThrow())
                        .toList()
                )
                .floor(roomOptional.get().getFloor())
                .build();

        Room updatedRoom = roomRepository.save(room);

        UpdateRoomOutput output = conversionService.convert(updatedRoom, UpdateRoomOutput.class);

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

        Room inputRoom = conversionService.convert(input, Room.RoomBuilder.class)
                .bedSizes(input.getBedSizes() != null ?
                        input.getBedSizes().stream().map(bed ->
                                bedRepository.findByBedSize(BedSize.getByCode(bed)).orElseThrow()
                        ).toList() : null)
                .build();

        JsonNode roomNode = mapper.valueToTree(currentRoom);
        JsonNode inputNode = mapper.valueToTree(inputRoom);

        JsonMergePatch patch = JsonMergePatch.fromJson(inputNode);
        Room updatedRoom = mapper.treeToValue(patch.apply(roomNode), Room.class);

        roomRepository.save(updatedRoom);

        PartialUpdateRoomOutput output = conversionService.convert(updatedRoom, PartialUpdateRoomOutput.class);

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
