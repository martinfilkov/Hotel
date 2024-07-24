package com.tinqinacademy.hotel.persistence.repositories;

import com.tinqinacademy.hotel.persistence.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RoomRepository extends JpaRepository<Room, UUID> {
}
