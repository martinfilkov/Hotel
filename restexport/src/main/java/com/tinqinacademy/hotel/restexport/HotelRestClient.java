package com.tinqinacademy.hotel.restexport;

import com.tinqinacademy.hotel.api.operations.base.URLMapping;
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
import feign.Param;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Optional;

@FeignClient("hotel-service")
public interface HotelRestClient {
    //Hotel
    @GetMapping(URLMapping.GET_IDS)
    ResponseEntity<GetRoomIdsOutput> getIds(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param(value = "bedSize") Optional<String> bedSize,
            @Param(value = "bathroomType") Optional<String> bathRoomType
    );

    @GetMapping(URLMapping.GET_ROOM)
    ResponseEntity<RoomByIdOutput> getRoom(@Param("roomId") String roomId);

    @PostMapping(URLMapping.BOOK_ROOM)
    ResponseEntity<BookRoomOutput> bookRoom(@Param("roomId") String roomId, BookRoomInput request);

    @DeleteMapping(URLMapping.UNBOOK_ROOM)
    ResponseEntity<UnbookRoomOutput> unbookRoom(@Param("bookingId") String bookingId);

    //System
    @PostMapping(URLMapping.REGISTER_VISITOR)
    ResponseEntity<RegisterVisitorOutput> register(RegisterVisitorInputList input);

    @GetMapping(URLMapping.INFO_REGISTRY)
    ResponseEntity<InfoRegisterOutputList> infoRegistry(
            @Param(value = "startDate") LocalDate startDate,
            @Param(value = "endDate") LocalDate endDate,
            @Param(value = "roomNumber") String roomNumber,
            @Param(value = "firstName") String firstName,
            @Param(value = "lastName") String lastName,
            @Param(value = "phone") String phone,
            @Param(value = "idCardNumber") String idCardNumber,
            @Param(value = "idCardValidity") String idCardValidity,
            @Param(value = "idCardIssueAuthority") String idCardIssueAuthority,
            @Param(value = "idCardIssueDate") String idCardIssueDate
    );

    @PostMapping(URLMapping.CREATE_ROOM)
    ResponseEntity<CreateRoomOutput> create(CreateRoomInput input);

    @PutMapping(URLMapping.UPDATE_ROOM)
    ResponseEntity<UpdateRoomOutput> update(@Param("id") String id, UpdateRoomInput request);

    @PatchMapping(path = URLMapping.PARTIAL_UPDATE_ROOM, consumes = "application/json-patch+json")
    ResponseEntity<PartialUpdateRoomOutput> partialUpdate(@Param("id") String id, PartialUpdateRoomInput request);

    @DeleteMapping(URLMapping.DELETE_ROOM)
    ResponseEntity<DeleteRoomOutput> delete(@Param("id") String id);
}
