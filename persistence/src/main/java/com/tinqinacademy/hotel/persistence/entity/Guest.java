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
public class Guest implements Entity {
    private UUID id;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String idCardValidity;
    private String idCardIssueAuthority;
    private String idCardIssueDate;
    private LocalDate birthDate;
}
