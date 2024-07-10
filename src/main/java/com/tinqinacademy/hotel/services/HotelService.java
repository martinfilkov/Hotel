package com.tinqinacademy.hotel.services;

import com.tinqinacademy.hotel.operations.hotel.bookroom.BookRoomInput;
import com.tinqinacademy.hotel.operations.hotel.bookroom.BookRoomOutput;
import com.tinqinacademy.hotel.operations.hotel.getroomids.GetRoomIdsInput;
import com.tinqinacademy.hotel.operations.hotel.getroomids.GetRoomIdsOutput;
import com.tinqinacademy.hotel.operations.hotel.roombyid.RoomByIdInput;
import com.tinqinacademy.hotel.operations.hotel.roombyid.RoomByIdOutput;
import com.tinqinacademy.hotel.operations.hotel.unbookroom.UnbookRoomInput;
import com.tinqinacademy.hotel.operations.hotel.unbookroom.UnbookRoomOutput;

public interface HotelService {
    GetRoomIdsOutput getRoomIds(GetRoomIdsInput input);
    RoomByIdOutput getRoom(RoomByIdInput input);
    BookRoomOutput bookRoom(BookRoomInput input);
    UnbookRoomOutput unbookRoom(UnbookRoomInput input);
}
