package com.tinqinacademy.hotel.persistence.repositories;

import com.tinqinacademy.hotel.persistence.entities.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, UUID> {
    Boolean existsByRoomIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(UUID roomId, LocalDate endDate, LocalDate startDate);

    @Query(value = """
            SELECT res.* FROM reservations res
            JOIN rooms r ON res.room_id = r.id
            WHERE r.room_number = :roomNumber
            AND res.start_date = :startDate
            AND res.end_date = :endDate
            """, nativeQuery = true)
    Optional<Reservation> findAvailableRoomByRoomNumberAndPeriod(@Param("roomNumber") String roomNumber,
                                                                 @Param("startDate") LocalDate startDate,
                                                                 @Param("endDate") LocalDate endDate);


    List<Reservation> findByRoomId(UUID roomId);
}
