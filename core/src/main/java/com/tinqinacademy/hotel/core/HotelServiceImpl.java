package com.tinqinacademy.hotel.core;

import com.tinqinacademy.hotel.api.model.BathroomType;
import com.tinqinacademy.hotel.api.model.BedSize;
import com.tinqinacademy.hotel.api.operations.hotel.bookroom.BookRoomInput;
import com.tinqinacademy.hotel.api.operations.hotel.bookroom.BookRoomOutput;
import com.tinqinacademy.hotel.api.operations.hotel.getroomids.GetRoomIdsInput;
import com.tinqinacademy.hotel.api.operations.hotel.getroomids.GetRoomIdsOutput;
import com.tinqinacademy.hotel.api.operations.hotel.roombyid.RoomByIdInput;
import com.tinqinacademy.hotel.api.operations.hotel.roombyid.RoomByIdOutput;
import com.tinqinacademy.hotel.api.operations.hotel.unbookroom.UnbookRoomInput;
import com.tinqinacademy.hotel.api.operations.hotel.unbookroom.UnbookRoomOutput;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class HotelServiceImpl implements HotelService{
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
        RoomByIdOutput output = RoomByIdOutput.builder()
                .id(input.getId())
                .floor(3)
                .bathroomType(BathroomType.PRIVATE)
                .bedSize(BedSize.DOUBLE)
                .datesOccupied(new ArrayList<>())
                .price(BigDecimal.TEN)
                .build();

        log.info("End getRoom output: {}", output);
        return output;
    }

    @Override
    public BookRoomOutput bookRoom(final BookRoomInput input) {
        log.info("Start bookRoom input: {}", input);
        BookRoomOutput output = new BookRoomOutput();
        log.info("End bookRoom output: {}", output);
        return output;
    }

    @Override
    public UnbookRoomOutput unbookRoom(UnbookRoomInput input) {
        log.info("Start unbookRoom input: {}", input);
        UnbookRoomOutput output = new UnbookRoomOutput();
        log.info("End unbookRoom output: {}", output);
        return output;
    }
}
