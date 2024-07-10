package com.tinqinacademy.hotel.services;

import com.tinqinacademy.hotel.operations.system.createroom.CreateRoomInput;
import com.tinqinacademy.hotel.operations.system.createroom.CreateRoomOutput;
import com.tinqinacademy.hotel.operations.system.deleteroom.DeleteRoomInput;
import com.tinqinacademy.hotel.operations.system.deleteroom.DeleteRoomOutput;
import com.tinqinacademy.hotel.operations.system.inforregister.InfoRegisterInput;
import com.tinqinacademy.hotel.operations.system.inforregister.InfoRegisterOutput;
import com.tinqinacademy.hotel.operations.system.partialupdate.PartialUpdateRoomInput;
import com.tinqinacademy.hotel.operations.system.partialupdate.PartialUpdateRoomOutput;
import com.tinqinacademy.hotel.operations.system.registervisitor.RegisterVisitorInput;
import com.tinqinacademy.hotel.operations.system.registervisitor.RegisterVisitorOutput;
import com.tinqinacademy.hotel.operations.system.updateroom.UpdateRoomInput;
import com.tinqinacademy.hotel.operations.system.updateroom.UpdateRoomOutput;

public interface SystemService {
    RegisterVisitorOutput registerVisitor(RegisterVisitorInput input);
    InfoRegisterOutput getRegisterInfo(InfoRegisterInput input);
    CreateRoomOutput createRoom(CreateRoomInput input);
    UpdateRoomOutput updateRoom(UpdateRoomInput input);
    PartialUpdateRoomOutput partialUpdateRoom(PartialUpdateRoomInput input);
    DeleteRoomOutput deleteRoom(DeleteRoomInput input);
}
