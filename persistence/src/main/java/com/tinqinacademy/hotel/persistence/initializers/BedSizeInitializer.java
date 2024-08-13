package com.tinqinacademy.hotel.persistence.initializers;

import com.tinqinacademy.hotel.persistence.entities.Bed;
import com.tinqinacademy.hotel.persistence.models.BedSize;
import com.tinqinacademy.hotel.persistence.repositories.BedRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
public class BedSizeInitializer implements ApplicationRunner {
    private final BedRepository bedRepository;

    @Autowired
    public BedSizeInitializer(BedRepository bedRepository) {
        this.bedRepository = bedRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        List<Bed> beds = bedRepository.findAll();
        List<BedSize> currentBedSizes = beds.stream().map(Bed::getBedSize).toList();
        Set<BedSize> validBedSizes = EnumSet.allOf(BedSize.class);

        currentBedSizes.stream()
                .filter(bedSize -> !validBedSizes.contains(bedSize))
                .forEach(bedSize -> {
                    bedRepository.deleteByBedSize(bedSize);
                    log.info("Removed invalid bed size: {}", bedSize);
                });

        log.info("Current bed sizes in the database: {}", currentBedSizes);

        EnumSet.allOf(BedSize.class).stream()
                .filter(bedSize -> !bedSize.equals(BedSize.UNKNOWN))
                .filter(bedSize -> !currentBedSizes.contains(bedSize))
                .map(bedSize -> Bed.builder()
                        .bedSize(bedSize)
                        .count(bedSize.getCount())
                        .build())
                .forEach(bed -> {
                    bedRepository.save(bed);
                    log.info("Added missing bed size: {}", bed.getBedSize());
                });
    }
}