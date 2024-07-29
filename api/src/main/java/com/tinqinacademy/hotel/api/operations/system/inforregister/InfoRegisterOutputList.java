package com.tinqinacademy.hotel.api.operations.system.inforregister;

import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class InfoRegisterOutputList {
    List<InfoRegisterOutput> visitors;
}
