package com.tinqinacademy.hotel.rest.controllers;

import com.tinqinacademy.hotel.api.operations.base.Errors;
import com.tinqinacademy.hotel.api.operations.hotel.bookroom.BookRoomInput;
import com.tinqinacademy.hotel.api.operations.hotel.bookroom.BookRoomOperation;
import com.tinqinacademy.hotel.api.operations.hotel.bookroom.BookRoomOutput;
import com.tinqinacademy.hotel.api.operations.hotel.getroomids.GetRoomIdsInput;
import com.tinqinacademy.hotel.api.operations.hotel.getroomids.GetRoomIdsOperation;
import com.tinqinacademy.hotel.api.operations.hotel.getroomids.GetRoomIdsOutput;
import com.tinqinacademy.hotel.api.operations.hotel.roombyid.RoomByIdInput;
import com.tinqinacademy.hotel.api.operations.hotel.roombyid.RoomByIdOperation;
import com.tinqinacademy.hotel.api.operations.hotel.roombyid.RoomByIdOutput;
import com.tinqinacademy.hotel.api.operations.hotel.unbookroom.UnbookRoomInput;
import com.tinqinacademy.hotel.api.operations.hotel.unbookroom.UnbookRoomOperation;
import com.tinqinacademy.hotel.api.operations.hotel.unbookroom.UnbookRoomOutput;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.vavr.control.Either;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class HotelController extends BaseController {
    private final GetRoomIdsOperation getRoomIdsOperation;
    private final RoomByIdOperation roomByIdOperation;
    private final BookRoomOperation bookRoomOperation;
    private final UnbookRoomOperation unbookRoomOperation;

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully returned ids"),
            @ApiResponse(responseCode = "403", description = "User not authorized")
    })
    @GetMapping(URLMapping.GET_IDS)
    public ResponseEntity<?> getIds(
            @RequestParam("startDate") LocalDate startDate,
            @RequestParam("endDate") LocalDate endDate,
            @RequestParam(value = "bedSize", required = false) Optional<String> bedSize,
            @RequestParam(value = "bathroomType", required = false) Optional<String> bathRoomType
    ) {
        GetRoomIdsInput input = GetRoomIdsInput.builder()
                .startDate(startDate)
                .endDate(endDate)
                .bathroomType(bathRoomType)
                .bedSize(bedSize)
                .build();

        Either<Errors, GetRoomIdsOutput> output = getRoomIdsOperation.process(input);
        return handleResponse(output, HttpStatus.OK);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully found room"),
            @ApiResponse(responseCode = "403", description = "User not authorized"),
            @ApiResponse(responseCode = "404", description = "Room not found")
    })
    @GetMapping(URLMapping.GET_ROOM)
    public ResponseEntity<?> getRoom(@PathVariable String roomId) {
        RoomByIdInput input = RoomByIdInput.builder()
                .id(roomId)
                .build();

        Either<Errors, RoomByIdOutput> output = roomByIdOperation.process(input);

        return handleResponse(output, HttpStatus.OK);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully booked room"),
            @ApiResponse(responseCode = "403", description = "User not authorized")
    })
    @PostMapping(URLMapping.BOOK_ROOM)
    public ResponseEntity<?> bookRoom(@PathVariable String roomId,
                                      @RequestBody BookRoomInput request) {
        BookRoomInput input = request.toBuilder()
                .roomId(roomId)
                .build();

        Either<Errors, BookRoomOutput> output = bookRoomOperation.process(input);

        return handleResponse(output, HttpStatus.CREATED);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Successfully unbooked room"),
            @ApiResponse(responseCode = "403", description = "User not authorized"),
            @ApiResponse(responseCode = "404", description = "Room not found")
    })
    @DeleteMapping(URLMapping.UNBOOK_ROOM)
    public ResponseEntity<?> unbookRoom(@PathVariable String bookingId) {
        UnbookRoomInput input = UnbookRoomInput.builder()
                .bookingId(bookingId)
                .build();

        Either<Errors, UnbookRoomOutput> output = unbookRoomOperation.process(input);

        return handleResponse(output, HttpStatus.ACCEPTED);
    }
}


