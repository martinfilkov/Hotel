package com.tinqinacademy.hotel.persistence.initializer;

import com.tinqinacademy.hotel.persistence.entity.User;
import com.tinqinacademy.hotel.persistence.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class UserInitializer implements ApplicationRunner {
    private final UserRepository userRepository;

    @Autowired
    public UserInitializer(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        long count = userRepository.count();
        log.info("User count on startup: {}", count);
        if (count == 0) {
            List<User> users = Arrays.asList(
                    User.builder()
                            .username("mfilkov")
                            .password("password")
                            .birthDate(LocalDate.of(2003, 10, 27))
                            .phoneNumber("0896225789")
                            .email("martinfilkov1@gmail.com")
                            .build(),
                    User.builder()
                            .username("jdoe")
                            .password("password")
                            .birthDate(LocalDate.of(1985, 5, 15))
                            .phoneNumber("0987654321")
                            .email("jdoe@example.com")
                            .build(),
                    User.builder()
                            .username("asmith")
                            .password("password")
                            .birthDate(LocalDate.of(1995, 3, 30))
                            .phoneNumber("1122334455")
                            .email("asmith@example.com")
                            .build(),
                    User.builder()
                            .username("bwilliams")
                            .password("password")
                            .birthDate(LocalDate.of(2015, 7, 20))
                            .phoneNumber("6677889900")
                            .email("bwilliams@example.com")
                            .build()
            );
            userRepository.saveAll(users);
            log.info("Added users: {}", users);
        }
    }
}
