package com.tinqinacademy.hotel.persistence.repositories;

import com.tinqinacademy.hotel.persistence.entities.Bed;
import com.tinqinacademy.hotel.persistence.models.BedSize;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BedRepository extends JpaRepository<Bed, UUID> {
    List<Bed> findAllByBedSizeIn(List<BedSize> bedSize);
    Optional<Bed> findByBedSize(BedSize bedSize);
    void deleteByBedSize(BedSize bedSize);
}
