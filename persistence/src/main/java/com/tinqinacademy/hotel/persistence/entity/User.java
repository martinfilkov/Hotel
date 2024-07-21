package com.tinqinacademy.hotel.persistence.entity;

import lombok.*;

import java.util.Date;
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
    private Date birthDate;
    private String phoneNumber;
}
