package com.tinqinacademy.hotel.persistence.repositories;

import com.tinqinacademy.hotel.persistence.entity.Reservation;

import java.time.LocalDate;
import java.util.UUID;

public interface ReservationRepository extends BaseRepository<Reservation, UUID> {
    boolean existsByRoomIdAndDateRange(UUID roomId, LocalDate startDate, LocalDate endDate);
}
