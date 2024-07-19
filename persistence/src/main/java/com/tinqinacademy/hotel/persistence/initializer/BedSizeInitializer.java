package com.tinqinacademy.hotel.persistence.initializer;

import com.tinqinacademy.hotel.persistence.entity.Bed;
import com.tinqinacademy.hotel.persistence.model.BedSize;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.EnumSet;
import java.util.List;

@Slf4j
@Component
public class BedSizeInitializer implements ApplicationRunner {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public BedSizeInitializer(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(ApplicationArguments args) {
        String checkSizes = "SELECT * FROM beds";

        List<Bed> beds = jdbcTemplate.query(checkSizes, (rs, rowNum) -> Bed.builder()
                .id(rs.getInt("id"))
                .bedSize(BedSize.getByCode(rs.getString("bed_size")))
                .count(rs.getInt("count"))
                .build());
        List<BedSize> currentBedSizes = beds.stream().map(Bed::getBedSize).toList();
        log.info("Current bed sizes in the database: {}", currentBedSizes);

        EnumSet.allOf(BedSize.class).stream()
                .filter(bedSize -> !bedSize.equals(BedSize.UNKNOWN))
                .filter(bedSize -> !currentBedSizes.contains(bedSize))
                .map(bedSize -> Bed.builder()
                        .bedSize(bedSize)
                        .count(bedSize.getCount())
                        .build())
                .forEach(bed -> {
                    jdbcTemplate.update("INSERT INTO beds(bed_size, count) VALUES(?,?)",
                            bed.getBedSize().toString(),
                            bed.getCount()
                    );
                    log.info("Added missing bed size: {}", bed.getBedSize());
                });
    }
}