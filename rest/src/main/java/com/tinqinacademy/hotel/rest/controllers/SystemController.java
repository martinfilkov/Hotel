package com.tinqinacademy.hotel.rest.controllers;

import com.tinqinacademy.hotel.api.operations.system.createroom.CreateRoomInput;
import com.tinqinacademy.hotel.api.operations.system.createroom.CreateRoomOutput;
import com.tinqinacademy.hotel.api.operations.system.createroom.CreateRoomProcess;
import com.tinqinacademy.hotel.api.operations.system.deleteroom.DeleteRoomInput;
import com.tinqinacademy.hotel.api.operations.system.deleteroom.DeleteRoomOutput;
import com.tinqinacademy.hotel.api.operations.system.deleteroom.DeleteRoomProcess;
import com.tinqinacademy.hotel.api.operations.system.inforregister.GetRegisterInfoProcess;
import com.tinqinacademy.hotel.api.operations.system.inforregister.InfoRegisterInput;
import com.tinqinacademy.hotel.api.operations.system.inforregister.InfoRegisterOutputList;
import com.tinqinacademy.hotel.api.operations.system.partialupdate.PartialUpdateProcess;
import com.tinqinacademy.hotel.api.operations.system.partialupdate.PartialUpdateRoomInput;
import com.tinqinacademy.hotel.api.operations.system.partialupdate.PartialUpdateRoomOutput;
import com.tinqinacademy.hotel.api.operations.system.registervisitor.RegisterVisitorInputList;
import com.tinqinacademy.hotel.api.operations.system.registervisitor.RegisterVisitorOutput;
import com.tinqinacademy.hotel.api.operations.system.registervisitor.RegisterVisitorProcess;
import com.tinqinacademy.hotel.api.operations.system.updateroom.UpdateRoomInput;
import com.tinqinacademy.hotel.api.operations.system.updateroom.UpdateRoomOutput;
import com.tinqinacademy.hotel.api.operations.system.updateroom.UpdateRoomProcess;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
public class SystemController {
    private final CreateRoomProcess createRoomProcess;
    private final DeleteRoomProcess deleteRoomProcess;
    private final PartialUpdateProcess partialUpdateProcess;
    private final RegisterVisitorProcess registerVisitorProcess;
    private final UpdateRoomProcess updateRoomProcess;
    private final GetRegisterInfoProcess getRegisterInfoProcess;

    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Registered a visitor"),
            @ApiResponse(responseCode = "403", description = "User not authorized")
    })
    @PostMapping(URLMapping.REGISTER_VISITOR)
    public ResponseEntity<RegisterVisitorOutput> register(@Valid @RequestBody RegisterVisitorInputList input) {
        RegisterVisitorOutput output = registerVisitorProcess.process(input);

        return new ResponseEntity<>(output, HttpStatus.CREATED);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully returned visitor"),
            @ApiResponse(responseCode = "403", description = "User not authorized"),
            @ApiResponse(responseCode = "404", description = "Visitor not found")
    })
    @GetMapping(URLMapping.INFO_REGISTRY)
    public ResponseEntity<InfoRegisterOutputList> infoRegistry(
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

        InfoRegisterOutputList output = getRegisterInfoProcess.process(input);

        return ResponseEntity.ok(output);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully created a room"),
            @ApiResponse(responseCode = "403", description = "User not authorized")
    })
    @PostMapping(URLMapping.CREATE_ROOM)
    public ResponseEntity<CreateRoomOutput> create(@Valid @RequestBody CreateRoomInput input) {
        CreateRoomOutput output = createRoomProcess.process(input);

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

        UpdateRoomOutput output = updateRoomProcess.process(input);

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

        PartialUpdateRoomOutput output = partialUpdateProcess.process(input);

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

        DeleteRoomOutput output = deleteRoomProcess.process(input);
        return new ResponseEntity<>(output, HttpStatus.ACCEPTED);
    }

}
