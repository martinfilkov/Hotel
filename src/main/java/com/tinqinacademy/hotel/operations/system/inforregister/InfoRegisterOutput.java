package com.tinqinacademy.hotel.operations.system.inforregister;

import lombok.*;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class InfoRegisterOutput {
    private LocalDate startDate;
    private LocalDate endDate;
    private String firstName;
    private String lastName;
    private String phone;
    private String idCardNumber;
    private String idCardValidity;
    private String idCardIssueAuthority;
    private String idCardIssueDate;
}
