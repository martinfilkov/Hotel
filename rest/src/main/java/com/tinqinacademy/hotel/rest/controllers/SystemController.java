package com.tinqinacademy.hotel.rest.controllers;

import com.tinqinacademy.hotel.api.operations.base.Errors;
import com.tinqinacademy.hotel.api.operations.base.URLMapping;
import com.tinqinacademy.hotel.api.operations.system.createroom.CreateRoomInput;
import com.tinqinacademy.hotel.api.operations.system.createroom.CreateRoomOperation;
import com.tinqinacademy.hotel.api.operations.system.createroom.CreateRoomOutput;
import com.tinqinacademy.hotel.api.operations.system.deleteroom.DeleteRoomInput;
import com.tinqinacademy.hotel.api.operations.system.deleteroom.DeleteRoomOperation;
import com.tinqinacademy.hotel.api.operations.system.deleteroom.DeleteRoomOutput;
import com.tinqinacademy.hotel.api.operations.system.inforregister.GetRegisterInfoOperation;
import com.tinqinacademy.hotel.api.operations.system.inforregister.InfoRegisterInput;
import com.tinqinacademy.hotel.api.operations.system.inforregister.InfoRegisterOutputList;
import com.tinqinacademy.hotel.api.operations.system.partialupdate.PartialUpdateOperation;
import com.tinqinacademy.hotel.api.operations.system.partialupdate.PartialUpdateRoomInput;
import com.tinqinacademy.hotel.api.operations.system.partialupdate.PartialUpdateRoomOutput;
import com.tinqinacademy.hotel.api.operations.system.registervisitor.RegisterVisitorInputList;
import com.tinqinacademy.hotel.api.operations.system.registervisitor.RegisterVisitorOperation;
import com.tinqinacademy.hotel.api.operations.system.registervisitor.RegisterVisitorOutput;
import com.tinqinacademy.hotel.api.operations.system.updateroom.UpdateRoomInput;
import com.tinqinacademy.hotel.api.operations.system.updateroom.UpdateRoomOperation;
import com.tinqinacademy.hotel.api.operations.system.updateroom.UpdateRoomOutput;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.vavr.control.Either;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
public class SystemController extends BaseController {
    private final CreateRoomOperation createRoomOperation;
    private final DeleteRoomOperation deleteRoomOperation;
    private final PartialUpdateOperation partialUpdateOperation;
    private final RegisterVisitorOperation registerVisitorOperation;
    private final UpdateRoomOperation updateRoomOperation;
    private final GetRegisterInfoOperation getRegisterInfoOperation;

    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Registered a visitor"),
            @ApiResponse(responseCode = "403", description = "User not authorized")
    })
    @PostMapping(URLMapping.REGISTER_VISITOR)
    public ResponseEntity<?> register(@RequestBody RegisterVisitorInputList input) {
        Either<Errors, RegisterVisitorOutput> output = registerVisitorOperation.process(input);

        return handleResponse(output, HttpStatus.CREATED);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully returned visitor"),
            @ApiResponse(responseCode = "403", description = "User not authorized"),
            @ApiResponse(responseCode = "404", description = "Visitor not found")
    })
    @GetMapping(URLMapping.INFO_REGISTRY)
    public ResponseEntity<?> infoRegistry(
            @RequestParam(value = "startDate") LocalDate startDate,
            @RequestParam(value = "endDate") LocalDate endDate,
            @RequestParam(value = "roomNumber") String roomNumber,
            @RequestParam(value = "firstName", required = false) String firstName,
            @RequestParam(value = "lastName", required = false) String lastName,
            @RequestParam(value = "phone", required = false) String phone,
            @RequestParam(value = "idCardNumber", required = false) String idCardNumber,
            @RequestParam(value = "idCardValidity", required = false) String idCardValidity,
            @RequestParam(value = "idCardIssueAuthority", required = false) String idCardIssueAuthority,
            @RequestParam(value = "idCardIssueDate", required = false) String idCardIssueDate
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

        Either<Errors, InfoRegisterOutputList> output = getRegisterInfoOperation.process(input);

        return handleResponse(output, HttpStatus.OK);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully created a room"),
            @ApiResponse(responseCode = "403", description = "User not authorized")
    })
    @PostMapping(URLMapping.CREATE_ROOM)
    public ResponseEntity<?> create(@RequestBody CreateRoomInput input) {
        Either<Errors, CreateRoomOutput> output = createRoomOperation.process(input);

        return handleResponse(output, HttpStatus.CREATED);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated room"),
            @ApiResponse(responseCode = "403", description = "User not authorized"),
            @ApiResponse(responseCode = "404", description = "Room not found")
    })
    @PutMapping(URLMapping.UPDATE_ROOM)
    public ResponseEntity<?> update(@PathVariable("roomId") String id,
                                    @RequestBody UpdateRoomInput request) {
        UpdateRoomInput input = request.toBuilder()
                .roomId(id)
                .build();

        Either<Errors, UpdateRoomOutput> output = updateRoomOperation.process(input);

        return handleResponse(output, HttpStatus.OK);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated room"),
            @ApiResponse(responseCode = "403", description = "User not authorized"),
            @ApiResponse(responseCode = "404", description = "Room not found")
    })
    @PatchMapping(path = URLMapping.PARTIAL_UPDATE_ROOM, consumes = "application/json-patch+json")
    public ResponseEntity<?> partialUpdate(@PathVariable("roomId") String id,
                                           @RequestBody PartialUpdateRoomInput request) {
        PartialUpdateRoomInput input = request.toBuilder()
                .roomId(id)
                .build();

        Either<Errors, PartialUpdateRoomOutput> output = partialUpdateOperation.process(input);

        return handleResponse(output, HttpStatus.OK);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Successfully deleted room"),
            @ApiResponse(responseCode = "403", description = "User not authorized"),
            @ApiResponse(responseCode = "404", description = "Room not found")
    })
    @DeleteMapping(URLMapping.DELETE_ROOM)
    public ResponseEntity<?> delete(@PathVariable("roomId") String id) {
        DeleteRoomInput input = DeleteRoomInput.builder()
                .id(id)
                .build();

        Either<Errors, DeleteRoomOutput> output = deleteRoomOperation.process(input);
        return handleResponse(output, HttpStatus.ACCEPTED);
    }

}
