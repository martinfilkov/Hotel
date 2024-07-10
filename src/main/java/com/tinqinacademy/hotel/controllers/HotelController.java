package com.tinqinacademy.hotel.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tinqinacademy.hotel.operations.hotel.bookroom.BookRoomInput;
import com.tinqinacademy.hotel.operations.hotel.bookroom.BookRoomOutput;
import com.tinqinacademy.hotel.operations.hotel.getroomids.GetRoomIdsInput;
import com.tinqinacademy.hotel.operations.hotel.getroomids.GetRoomIdsOutput;
import com.tinqinacademy.hotel.operations.hotel.roombyid.RoomByIdInput;
import com.tinqinacademy.hotel.operations.hotel.roombyid.RoomByIdOutput;
import com.tinqinacademy.hotel.operations.hotel.unbookroom.UnbookRoomInput;
import com.tinqinacademy.hotel.operations.hotel.unbookroom.UnbookRoomOutput;
import com.tinqinacademy.hotel.services.HotelService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/hotel")
@RequiredArgsConstructor
public class HotelController {
    private final HotelService hotelService;
    private final ObjectMapper objectMapper;

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully returned ids"),
            @ApiResponse(responseCode = "403", description = "User not authorized")
    })
    @GetMapping("/rooms")
    public ResponseEntity<GetRoomIdsOutput> getIds(
            @RequestParam("startDate") LocalDate startDate,
            @RequestParam("endDate") LocalDate endDate,
            @RequestParam("bedSize") String bedSize,
            @RequestParam("bathroomType") String bathRoomType
            ){
        GetRoomIdsInput input = GetRoomIdsInput.builder()
                .startDate(startDate)
                .endDate(endDate)
                .bathroomType(bathRoomType)
                .bedSize(bedSize)
                .build();

        GetRoomIdsOutput output = hotelService.getRoomIds(input);

        return ResponseEntity.ok(output);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully found room"),
            @ApiResponse(responseCode = "403", description = "User not authorized"),
            @ApiResponse(responseCode = "404", description = "Room not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<RoomByIdOutput> getRoom(@PathVariable String id){
        RoomByIdInput input = RoomByIdInput.builder()
                .id(id)
                .build();

        RoomByIdOutput output = hotelService.getRoom(input);

        return ResponseEntity.ok(output);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully booked room"),
            @ApiResponse(responseCode = "403", description = "User not authorized")
    })
    @PostMapping("/{id}")
    public ResponseEntity<BookRoomOutput> bookRoom(@PathVariable String id,
                                                   @Valid @RequestBody BookRoomInput request){
        BookRoomInput input = request.toBuilder()
                .roomId(id)
                .build();

        return new ResponseEntity<>(hotelService.bookRoom(input), HttpStatus.CREATED);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Successfully unbooked room"),
            @ApiResponse(responseCode = "403", description = "User not authorized"),
            @ApiResponse(responseCode = "404", description = "Room not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<UnbookRoomOutput> unbookRoom(@PathVariable String id){
        UnbookRoomInput input = UnbookRoomInput.builder()
                .bookingId(id)
                .build();

        UnbookRoomOutput output = hotelService.unbookRoom(input);

        return new ResponseEntity<>(output, HttpStatus.ACCEPTED);
    }
}


