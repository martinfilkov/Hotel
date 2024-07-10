package com.tinqinacademy.hotel.api.operations.hotel.roombyid;

import com.tinqinacademy.hotel.api.model.BathroomType;
import com.tinqinacademy.hotel.api.model.BedSize;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class RoomByIdOutput {
    private String id;
    private BigDecimal price;
    private Integer floor;
    private BedSize bedSize;
    private BathroomType bathroomType;
    private List<LocalDate> datesOccupied;
}
