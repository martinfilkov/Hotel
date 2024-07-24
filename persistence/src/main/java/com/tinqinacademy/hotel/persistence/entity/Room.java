package com.tinqinacademy.hotel.persistence.entity;

import com.tinqinacademy.hotel.persistence.model.BathroomType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
@Entity
@Table(name = "rooms")
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "bathroom_type", nullable = false)
    private BathroomType bathroomType;

    @Column(name = "floor", nullable = false)
    private Integer floor;

    @Column(name = "room_number", nullable = false, unique = true)
    private String roomNumber;

    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @ManyToMany
    @JoinTable(
            name = "room_to_bed",
            joinColumns = @JoinColumn(name = "room_id"),
            inverseJoinColumns = @JoinColumn(name = "bed_id")
    )
    private List<Bed> bedSizes;
}
