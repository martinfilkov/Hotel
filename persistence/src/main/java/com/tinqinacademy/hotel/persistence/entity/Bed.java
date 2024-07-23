package com.tinqinacademy.hotel.persistence.entity;

import com.tinqinacademy.hotel.persistence.model.BedSize;
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
@Table(name = "beds")
public class Bed {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "bed_size", unique = true, nullable = false)
    private BedSize bedSize;

    @Column(name = "capacity", nullable = false)
    private Integer count;
}
