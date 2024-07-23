package com.tinqinacademy.hotel.persistence.entity;

import jakarta.persistence.*;
import jakarta.persistence.Entity;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
@Entity
@Table(name = "guests")
public class Guest {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @Column(name = "id_card_validity", nullable = false)
    private String idCardValidity;

    @Column(name = "id_card_issue_authority", nullable = false)
    private String idCardIssueAuthority;

    @Column(name = "id_card_issue_date", nullable = false)
    private String idCardIssueDate;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;
}
