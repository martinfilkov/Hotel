package com.tinqinacademy.hotel.core.services.operations.hotel;

import com.tinqinacademy.hotel.api.operations.base.Errors;
import com.tinqinacademy.hotel.api.operations.hotel.getroomids.GetRoomIdsInput;
import com.tinqinacademy.hotel.api.operations.hotel.getroomids.GetRoomIdsOperation;
import com.tinqinacademy.hotel.api.operations.hotel.getroomids.GetRoomIdsOutput;
import com.tinqinacademy.hotel.core.services.operations.BaseOperationProcessor;
import com.tinqinacademy.hotel.core.utils.ErrorMapper;
import com.tinqinacademy.hotel.persistence.entity.Room;
import com.tinqinacademy.hotel.persistence.model.BathroomType;
import com.tinqinacademy.hotel.persistence.repositories.RoomRepository;
import io.vavr.control.Either;
import io.vavr.control.Try;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

import static io.vavr.API.*;

@Slf4j
@Service
public class GetRoomIdsOperationProcessor extends BaseOperationProcessor implements GetRoomIdsOperation {
    private final RoomRepository roomRepository;

    @Autowired
    public GetRoomIdsOperationProcessor(RoomRepository roomRepository,
                                        ConversionService conversionService,
                                        Validator validator,
                                        ErrorMapper errorMapper) {
        super(conversionService, validator, errorMapper);
        this.roomRepository = roomRepository;
    }

    @Override
    public Either<Errors, GetRoomIdsOutput> process(GetRoomIdsInput input) {
        return validateInput(input)
                .flatMap(this::getRoomIds);
    }

    private Either<Errors, GetRoomIdsOutput> getRoomIds(GetRoomIdsInput input){
        return Try.of(() ->
                {
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
                })
                .toEither()
                .mapLeft(throwable -> Match(throwable).of(
                        Case($(), ex -> errorMapper.handleError(ex, HttpStatus.BAD_REQUEST))));
    }
}
