package com.tinqinacademy.hotel.persistence.entity;

import com.tinqinacademy.hotel.persistence.model.BathroomType;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class Room {
    private UUID id;
    private BathroomType bathroomType;
    private Integer floor;
    private String roomNumber;
    private BigDecimal price;
}
