package com.tinqinacademy.hotel.api.operations.system.inforregister;

import com.tinqinacademy.hotel.api.operations.base.OperationOutput;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class InfoRegisterOutputList implements OperationOutput {
    List<InfoRegisterOutput> visitors;
}
