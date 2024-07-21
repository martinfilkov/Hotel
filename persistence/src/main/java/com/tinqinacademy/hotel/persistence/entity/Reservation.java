package com.tinqinacademy.hotel.persistence.entity;

import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class Reservation implements Entity {
    public UUID id;
    private LocalDate startDate;
    private LocalDate endDate;
    private UUID userId;
    private UUID roomId;
}
