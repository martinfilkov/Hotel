package com.tinqinacademy.hotel.rest.controllers;

import com.tinqinacademy.hotel.api.operations.hotel.bookroom.BookRoomInput;
import com.tinqinacademy.hotel.api.operations.hotel.bookroom.BookRoomOutput;
import com.tinqinacademy.hotel.api.operations.hotel.bookroom.BookRoomProcess;
import com.tinqinacademy.hotel.api.operations.hotel.getroomids.GetRoomIdsInput;
import com.tinqinacademy.hotel.api.operations.hotel.getroomids.GetRoomIdsOutput;
import com.tinqinacademy.hotel.api.operations.hotel.getroomids.GetRoomIdsProcess;
import com.tinqinacademy.hotel.api.operations.hotel.roombyid.RoomByIdInput;
import com.tinqinacademy.hotel.api.operations.hotel.roombyid.RoomByIdOutput;
import com.tinqinacademy.hotel.api.operations.hotel.roombyid.RoomByIdProcess;
import com.tinqinacademy.hotel.api.operations.hotel.unbookroom.UnbookRoomInput;
import com.tinqinacademy.hotel.api.operations.hotel.unbookroom.UnbookRoomOutput;
import com.tinqinacademy.hotel.api.operations.hotel.unbookroom.UnbookRoomProcess;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class HotelController {
    private final GetRoomIdsProcess getRoomIdsProcess;
    private final RoomByIdProcess roomByIdProcess;
    private final BookRoomProcess bookRoomProcess;
    private final UnbookRoomProcess unbookRoomProcess;

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully returned ids"),
            @ApiResponse(responseCode = "403", description = "User not authorized")
    })
    @GetMapping(URLMapping.GET_IDS)
    public ResponseEntity<GetRoomIdsOutput> getIds(
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

        GetRoomIdsOutput output = getRoomIdsProcess.process(input);

        return ResponseEntity.ok(output);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully found room"),
            @ApiResponse(responseCode = "403", description = "User not authorized"),
            @ApiResponse(responseCode = "404", description = "Room not found")
    })
    @GetMapping(URLMapping.GET_ROOM)
    public ResponseEntity<RoomByIdOutput> getRoom(@PathVariable String roomId) {
        RoomByIdInput input = RoomByIdInput.builder()
                .id(roomId)
                .build();

        RoomByIdOutput output = roomByIdProcess.process(input);

        return ResponseEntity.ok(output);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully booked room"),
            @ApiResponse(responseCode = "403", description = "User not authorized")
    })
    @PostMapping(URLMapping.BOOK_ROOM)
    public ResponseEntity<BookRoomOutput> bookRoom(@PathVariable String roomId,
                                                   @Valid @RequestBody BookRoomInput request) {
        BookRoomInput input = request.toBuilder()
                .roomId(roomId)
                .build();

        return new ResponseEntity<>(bookRoomProcess.process(input), HttpStatus.CREATED);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Successfully unbooked room"),
            @ApiResponse(responseCode = "403", description = "User not authorized"),
            @ApiResponse(responseCode = "404", description = "Room not found")
    })
    @DeleteMapping(URLMapping.UNBOOK_ROOM)
    public ResponseEntity<UnbookRoomOutput> unbookRoom(@PathVariable String bookingId) {
        UnbookRoomInput input = UnbookRoomInput.builder()
                .bookingId(bookingId)
                .build();

        UnbookRoomOutput output = unbookRoomProcess.process(input);

        return new ResponseEntity<>(output, HttpStatus.ACCEPTED);
    }
}


