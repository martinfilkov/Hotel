package com.tinqinacademy.hotel.persistence.repositories;

import com.tinqinacademy.hotel.persistence.entities.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface RoomRepository extends JpaRepository<Room, UUID> {
    @Query(value = """
                        SELECT r.* 
                        FROM rooms r 
                        LEFT JOIN reservations res ON r.id = res.room_id 
                            AND res.start_date <= :endDate 
                            AND res.end_date >= :startDate 
                        WHERE res.room_id IS NULL;
            """, nativeQuery = true)
    List<Room> findAvailableRooms(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    Boolean existsByRoomNumber(String roomNumber);
}
