package com.tinqinacademy.hotel.persistence.entity;

import com.tinqinacademy.hotel.persistence.model.BedSize;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class Bed {
    private Integer id;
    private BedSize bedSize;
    private Integer count;
}
