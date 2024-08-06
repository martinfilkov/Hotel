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
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Optional;

@FeignClient("hotel-service")
public interface HotelRestClient {
    // Hotel
    @GetMapping(URLMapping.GET_IDS)
    ResponseEntity<GetRoomIdsOutput> getIds(
            @RequestParam("startDate") LocalDate startDate,
            @RequestParam("endDate") LocalDate endDate,
            @RequestParam(value = "bedSize") Optional<String> bedSize,
            @RequestParam(value = "bathroomType") Optional<String> bathRoomType
    );

    @GetMapping(URLMapping.GET_ROOM)
    ResponseEntity<RoomByIdOutput> getRoom(@RequestParam("roomId") String roomId);

    @PostMapping(URLMapping.BOOK_ROOM)
    ResponseEntity<BookRoomOutput> bookRoom(@RequestParam("roomId") String roomId, @RequestBody BookRoomInput request);

    @DeleteMapping(URLMapping.UNBOOK_ROOM)
    ResponseEntity<UnbookRoomOutput> unbookRoom(@RequestParam("bookingId") String bookingId);

    // System
    @PostMapping(URLMapping.REGISTER_VISITOR)
    ResponseEntity<RegisterVisitorOutput> register(@RequestBody RegisterVisitorInputList input);

    @GetMapping(URLMapping.INFO_REGISTRY)
    ResponseEntity<InfoRegisterOutputList> infoRegistry(
            @RequestParam(value = "startDate") LocalDate startDate,
            @RequestParam(value = "endDate") LocalDate endDate,
            @RequestParam(value = "roomNumber") String roomNumber,
            @RequestParam(value = "firstName") String firstName,
            @RequestParam(value = "lastName") String lastName,
            @RequestParam(value = "phone") String phone,
            @RequestParam(value = "idCardNumber") String idCardNumber,
            @RequestParam(value = "idCardValidity") String idCardValidity,
            @RequestParam(value = "idCardIssueAuthority") String idCardIssueAuthority,
            @RequestParam(value = "idCardIssueDate") String idCardIssueDate
    );

    @PostMapping(URLMapping.CREATE_ROOM)
    ResponseEntity<CreateRoomOutput> create(@RequestBody CreateRoomInput input);

    @PutMapping(URLMapping.UPDATE_ROOM)
    ResponseEntity<UpdateRoomOutput> update(@RequestParam("id") String id, @RequestBody UpdateRoomInput request);

    @PatchMapping(path = URLMapping.PARTIAL_UPDATE_ROOM, consumes = "application/json-patch+json")
    ResponseEntity<PartialUpdateRoomOutput> partialUpdate(@RequestParam("id") String id, @RequestBody PartialUpdateRoomInput request);

    @DeleteMapping(URLMapping.DELETE_ROOM)
    ResponseEntity<DeleteRoomOutput> delete(@RequestParam("id") String id);
}
