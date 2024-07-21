package com.tinqinacademy.hotel.persistence.entity;

import lombok.*;

import java.util.Date;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class Reservation implements Entity {
    public UUID id;
    private Date startDate;
    private Date endDate;
    private UUID userId;
    private UUID roomId;
}
