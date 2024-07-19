package com.tinqinacademy.hotel.persistence.entity;

import com.tinqinacademy.hotel.persistence.model.BathroomType;
import com.tinqinacademy.hotel.persistence.model.BedSize;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class Room implements Entity{
    private UUID id;
    private BathroomType bathroomType;
    private Integer floor;
    private String roomNumber;
    private BigDecimal price;
    private List<BedSize> bedSizes;
}
