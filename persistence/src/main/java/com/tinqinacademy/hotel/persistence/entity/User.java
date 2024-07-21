package com.tinqinacademy.hotel.persistence.entity;

import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class User implements Entity{
    private UUID id;
    private String username;
    private String email;
    private String password;
    private LocalDate birthDate;
    private String phoneNumber;
}
