package com.tinqinacademy.hotel.persistence.initializer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Slf4j
@Component
public class UserInitializer implements ApplicationRunner {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserInitializer(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(ApplicationArguments args){
        String viewCount = "SELECT COUNT(*) FROM users";
        Long count = jdbcTemplate.queryForObject(viewCount, Long.class);
        log.info("User count on startup: {}", count);
        if(count == null || count == 0) {
            String insertUser = """
                    INSERT INTO users(username, password, birth_date, phone_number, email)
                    VALUES(?, ?, ?, ?, ?)
                    """;

            jdbcTemplate.update(insertUser, "mfilkov", "password", LocalDate.of(2003, 10, 27), "0896225789", "martinfilkov1@gmail.com");
            jdbcTemplate.update(insertUser, "jdoe", "password", LocalDate.of(1985, 5, 15), "0987654321", "jdoe@example.com");
            jdbcTemplate.update(insertUser, "asmith", "password", LocalDate.of(1995, 3, 30), "1122334455", "asmith@example.com");
            jdbcTemplate.update(insertUser, "bwilliams", "password", LocalDate.of(2015, 7, 20), "6677889900", "bwilliams@example.com");
        }
    }
}
