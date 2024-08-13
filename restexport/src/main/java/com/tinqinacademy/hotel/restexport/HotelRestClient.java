package com.tinqinacademy.hotel.restexport;

import com.tinqinacademy.hotel.api.operations.hotel.bookroom.BookRoomInput;
import com.tinqinacademy.hotel.api.operations.hotel.bookroom.BookRoomOutput;
import com.tinqinacademy.hotel.api.operations.hotel.getroomids.GetRoomIdsOutput;
import com.tinqinacademy.hotel.api.operations.hotel.roombyid.RoomByIdOutput;
import com.tinqinacademy.hotel.api.operations.hotel.unbookroom.UnbookRoomOutput;
import com.tinqinacademy.hotel.api.operations.system.createroom.CreateRoomInput;
import com.tinqinacademy.hotel.api.operations.system.createroom.CreateRoomOutput;
import com.tinqinacademy.hotel.api.operations.system.deleteroom.DeleteRoomOutput;
import com.tinqinacademy.hotel.api.operations.system.inforregister.InfoRegisterOutputList;
import com.tinqinacademy.hotel.api.operations.system.partialupdate.PartialUpdateRoomInput;
import com.tinqinacademy.hotel.api.operations.system.partialupdate.PartialUpdateRoomOutput;
import com.tinqinacademy.hotel.api.operations.system.registervisitor.RegisterVisitorInputList;
import com.tinqinacademy.hotel.api.operations.system.registervisitor.RegisterVisitorOutput;
import com.tinqinacademy.hotel.api.operations.system.updateroom.UpdateRoomInput;
import com.tinqinacademy.hotel.api.operations.system.updateroom.UpdateRoomOutput;
import feign.Headers;
import feign.Param;
import feign.RequestLine;

import java.time.LocalDate;
import java.util.Optional;

@Headers({"Content-Type: application/json"})
public interface HotelRestClient {

    // Hotel
    @RequestLine("GET /api/hotel/rooms?startDate={startDate}&endDate={endDate}&bedSize={bedSize}&bathroomType={bathroomType}")
    GetRoomIdsOutput getIds(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("bedSize") Optional<String> bedSize,
            @Param("bathroomType") Optional<String> bathRoomType
    );

    @RequestLine("GET /api/hotel/{roomId}")
    RoomByIdOutput getRoom(@Param("roomId") String roomId);

    @RequestLine("POST /api/hotel/{roomId}")
    BookRoomOutput bookRoom(@Param("roomId") String roomId, BookRoomInput request);

    @RequestLine("DELETE /api/hotel/{bookingId}")
    UnbookRoomOutput unbookRoom(@Param("bookingId") String bookingId);

    // System
    @RequestLine("POST /api/system/register")
    RegisterVisitorOutput register(RegisterVisitorInputList input);

    @RequestLine("GET /api/system/register?startDate={startDate}&endDate={endDate}&roomNumber={roomNumber}&firstName={firstName}&lastName={lastName}&phone={phone}&idCardNumber={idCardNumber}&idCardValidity={idCardValidity}&idCardIssueAuthority={idCardIssueAuthority}&idCardIssueDate={idCardIssueDate}")
    InfoRegisterOutputList infoRegistry(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("roomNumber") String roomNumber,
            @Param("firstName") String firstName,
            @Param("lastName") String lastName,
            @Param("phone") String phone,
            @Param("idCardNumber") String idCardNumber,
            @Param("idCardValidity") String idCardValidity,
            @Param("idCardIssueAuthority") String idCardIssueAuthority,
            @Param("idCardIssueDate") String idCardIssueDate
    );

    @RequestLine("POST /api/system/room")
    CreateRoomOutput create(CreateRoomInput input);

    @RequestLine("PUT /api/system/room/{roomId}")
    UpdateRoomOutput update(@Param("roomId") String roomId, UpdateRoomInput request);

    @RequestLine("PATCH /api/system/room/{roomId}")
//    @Headers("Content-Type: application/json-patch+json")
    PartialUpdateRoomOutput partialUpdate(@Param("roomId") String roomId, PartialUpdateRoomInput request);

    @RequestLine("DELETE /api/system/room/{roomId}")
    DeleteRoomOutput delete(@Param("roomId") String roomId);
}
