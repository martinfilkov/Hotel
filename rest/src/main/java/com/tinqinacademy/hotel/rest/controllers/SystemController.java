package com.tinqinacademy.hotel.rest.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tinqinacademy.hotel.api.operations.system.createroom.CreateRoomInput;
import com.tinqinacademy.hotel.api.operations.system.createroom.CreateRoomOutput;
import com.tinqinacademy.hotel.api.operations.system.deleteroom.DeleteRoomInput;
import com.tinqinacademy.hotel.api.operations.system.deleteroom.DeleteRoomOutput;
import com.tinqinacademy.hotel.api.operations.system.inforregister.InfoRegisterInput;
import com.tinqinacademy.hotel.api.operations.system.inforregister.InfoRegisterOutput;
import com.tinqinacademy.hotel.api.operations.system.partialupdate.PartialUpdateRoomInput;
import com.tinqinacademy.hotel.api.operations.system.partialupdate.PartialUpdateRoomOutput;
import com.tinqinacademy.hotel.api.operations.system.registervisitor.RegisterVisitorInputList;
import com.tinqinacademy.hotel.api.operations.system.registervisitor.RegisterVisitorOutput;
import com.tinqinacademy.hotel.api.operations.system.updateroom.UpdateRoomInput;
import com.tinqinacademy.hotel.api.operations.system.updateroom.UpdateRoomOutput;
import com.tinqinacademy.hotel.core.services.SystemService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class SystemController {
    private final SystemService systemService;
    private final ObjectMapper objectMapper;

    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Registered a visitor"),
            @ApiResponse(responseCode = "403", description = "User not authorized")
    })
    @PostMapping(URLMapping.REGISTER_VISITOR)
    public ResponseEntity<RegisterVisitorOutput> register(@Valid @RequestBody RegisterVisitorInputList input) {
        RegisterVisitorOutput output = systemService.registerVisitor(input);

        return new ResponseEntity<>(output, HttpStatus.CREATED);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully returned visitor"),
            @ApiResponse(responseCode = "403", description = "User not authorized"),
            @ApiResponse(responseCode = "404", description = "Visitor not found")
    })
    @GetMapping(URLMapping.INFO_REGISTRY)
    public ResponseEntity<List<InfoRegisterOutput>> infoRegistry(
            @RequestParam("startDate") LocalDate startDate,
            @RequestParam("endDate") LocalDate endDate,
            @RequestParam("firstName") String firstName,
            @RequestParam("lastName") String lastName,
            @RequestParam("phone") String phone,
            @RequestParam("idCardNumber") String idCardNumber,
            @RequestParam("idCardValidity") String idCardValidity,
            @RequestParam("idCardIssueAuthority") String idCardIssueAuthority,
            @RequestParam("idCardIssueDate") String idCardIssueDate,
            @RequestParam("roomNumber") String roomNumber
    ) {


        InfoRegisterInput input = InfoRegisterInput.builder()
                .startDate(startDate)
                .endDate(endDate)
                .firstName(firstName)
                .lastName(lastName)
                .phone(phone)
                .idCardNumber(idCardNumber)
                .idCardValidity(idCardValidity)
                .idCardIssueAuthority(idCardIssueAuthority)
                .idCardIssueDate(idCardIssueDate)
                .roomNumber(roomNumber)
                .build();

        InfoRegisterOutput output = systemService.getRegisterInfo(input);

        return ResponseEntity.ok(List.of(output));
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully created a room"),
            @ApiResponse(responseCode = "403", description = "User not authorized")
    })
    @PostMapping(URLMapping.CREATE_ROOM)
    public ResponseEntity<CreateRoomOutput> create(@Valid @RequestBody CreateRoomInput input) {
        CreateRoomOutput output = systemService.createRoom(input);

        return new ResponseEntity<>(output, HttpStatus.CREATED);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated room"),
            @ApiResponse(responseCode = "403", description = "User not authorized"),
            @ApiResponse(responseCode = "404", description = "Room not found")
    })
    @PutMapping(URLMapping.UPDATE_ROOM)
    public ResponseEntity<UpdateRoomOutput> update(@PathVariable("roomId") String id,
                                                   @Valid @RequestBody UpdateRoomInput request) {
        UpdateRoomInput input = request.toBuilder()
                .roomId(id)
                .build();

        UpdateRoomOutput output = systemService.updateRoom(input);

        return ResponseEntity.ok(output);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated room"),
            @ApiResponse(responseCode = "403", description = "User not authorized"),
            @ApiResponse(responseCode = "404", description = "Room not found")
    })
    @PatchMapping(path = URLMapping.PARTIAL_UPDATE_ROOM, consumes = "application/json-patch+json")
    public ResponseEntity<PartialUpdateRoomOutput> partialUpdate(@PathVariable("roomId") String id,
                                                                 @Valid @RequestBody PartialUpdateRoomInput request) {
        PartialUpdateRoomInput input = request.toBuilder()
                .roomId(id)
                .build();

        PartialUpdateRoomOutput output = systemService.partialUpdateRoom(input);

        return ResponseEntity.ok(output);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Successfully deleted room"),
            @ApiResponse(responseCode = "403", description = "User not authorized"),
            @ApiResponse(responseCode = "404", description = "Room not found")
    })
    @DeleteMapping(URLMapping.DELETE_ROOM)
    public ResponseEntity<DeleteRoomOutput> delete(@PathVariable("roomId") String id) {
        DeleteRoomInput input = DeleteRoomInput.builder()
                .id(id)
                .build();

        DeleteRoomOutput output = systemService.deleteRoom(input);
        return new ResponseEntity<>(output, HttpStatus.ACCEPTED);
    }

}
