package com.tinqinacademy.hotel.persistence.repositories;

import com.tinqinacademy.hotel.persistence.entity.Guest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface GuestRepository extends JpaRepository<Guest, UUID>, JpaSpecificationExecutor<Guest> {
    @Query(value = """
            SELECT g.* 
            FROM guests g 
            JOIN reservation_to_guest rg ON g.id = rg.guest_id 
            JOIN reservations res ON rg.reservation_id = res.id 
            JOIN rooms r ON res.room_id = r.id 
            WHERE r.room_number = :roomNumber 
                  AND res.start_date <= :endDate  
                  AND res.end_date >= :startDate   
            """, nativeQuery = true)
    List<Guest> findByDateRangeAndRoomNumber(@Param("startDate") LocalDate startDate,
                                             @Param("endDate") LocalDate endDate,
                                             @Param("roomNumber") String roomNumber);
}
