package com.tinqinacademy.hotel.core.services.operations.system;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import com.tinqinacademy.hotel.api.operations.base.Errors;
import com.tinqinacademy.hotel.api.operations.exceptions.NotFoundException;
import com.tinqinacademy.hotel.api.operations.system.partialupdate.PartialUpdateOperation;
import com.tinqinacademy.hotel.api.operations.system.partialupdate.PartialUpdateRoomInput;
import com.tinqinacademy.hotel.api.operations.system.partialupdate.PartialUpdateRoomOutput;
import com.tinqinacademy.hotel.core.services.operations.BaseOperationProcessor;
import com.tinqinacademy.hotel.core.utils.ErrorMapper;
import com.tinqinacademy.hotel.persistence.entities.Bed;
import com.tinqinacademy.hotel.persistence.entities.Room;
import com.tinqinacademy.hotel.persistence.models.BedSize;
import com.tinqinacademy.hotel.persistence.repositories.BedRepository;
import com.tinqinacademy.hotel.persistence.repositories.RoomRepository;
import io.vavr.control.Either;
import io.vavr.control.Try;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static io.vavr.API.*;
import static io.vavr.Predicates.instanceOf;

@Slf4j
@Service
public class PartialUpdateOperationProcessor extends BaseOperationProcessor implements PartialUpdateOperation {
    private final RoomRepository roomRepository;
    private final BedRepository bedRepository;
    private final ObjectMapper mapper;

    @Autowired
    public PartialUpdateOperationProcessor(RoomRepository roomRepository,
                                           BedRepository bedRepository,
                                           ConversionService conversionService,
                                           ObjectMapper mapper,
                                           Validator validator,
                                           ErrorMapper errorMapper) {
        super(conversionService, validator, errorMapper);
        this.roomRepository = roomRepository;
        this.bedRepository = bedRepository;
        this.mapper = mapper;
    }

    @Override
    public Either<Errors, PartialUpdateRoomOutput> process(PartialUpdateRoomInput input) {
        return validateInput(input)
                .flatMap(validated -> partialUpdateRoom(input));
    }

    private Either<Errors, PartialUpdateRoomOutput> partialUpdateRoom(PartialUpdateRoomInput input) {
        return Try.of(() -> {
                    log.info("Start partialUpdateRoom input: {}", input);

                    Room currentRoom = getIfRoomExists(input);

                    List<BedSize> bedSizes = getBedSizes(input);

                    List<Bed> beds = bedRepository.findAllByBedSizeIn(bedSizes);

                    beds = ObjectUtils.isEmpty(beds)
                            ? currentRoom.getBedSizes()
                            : beds;

                    Room inputRoom = conversionService.convert(input, Room.RoomBuilder.class)
                            .bedSizes(beds)
                            .build();

                    JsonNode roomNode = mapper.valueToTree(currentRoom);
                    JsonNode inputNode = mapper.valueToTree(inputRoom);

                    JsonMergePatch patch = JsonMergePatch.fromJson(inputNode);
                    Room updatedRoom = mapper.treeToValue(patch.apply(roomNode), Room.class);

                    roomRepository.save(updatedRoom);

                    PartialUpdateRoomOutput output = conversionService.convert(updatedRoom, PartialUpdateRoomOutput.class);

                    log.info("End partialUpdateRoom output: {}", output);
                    return output;
                })
                .toEither()
                .mapLeft(throwable -> Match(throwable).of(
                        Case($(instanceOf(NotFoundException.class)), ex -> errorMapper.handleError(ex, HttpStatus.NOT_FOUND)),
                        Case($(), ex -> errorMapper.handleError(ex, HttpStatus.BAD_REQUEST))
                ));
    }

    private Room getIfRoomExists(PartialUpdateRoomInput input) {
        log.info("Try to get room with id: {}", input.getRoomId());

        Optional<Room> roomOptional = roomRepository.findById(UUID.fromString(input.getRoomId()));
        if (roomOptional.isEmpty()) {
            throw new NotFoundException("Room with id " + input.getRoomId() + " not found");
        }

        log.info("Room with id {} exists", input.getRoomId());
        return roomOptional.get();
    }

    private List<BedSize> getBedSizes(PartialUpdateRoomInput input) {
        log.info("Get bed sizes from strings");
        List<BedSize> bedSizes = new ArrayList<>();
        if (input.getBedSizes() != null
                && !ObjectUtils.isEmpty(input.getBedSizes())) {
            bedSizes = input.getBedSizes()
                    .stream()
                    .map(BedSize::getByCode)
                    .toList();
        }
        log.info("Got all bed sizes");
        return bedSizes;
    }
}
