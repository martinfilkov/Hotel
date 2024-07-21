package com.tinqinacademy.hotel.persistence.repositories;

import com.tinqinacademy.hotel.persistence.entity.Reservation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class ReservationRepositoryImpl implements ReservationRepository {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ReservationRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Reservation save(Reservation entity) {
        String insertReservation = """
                INSERT INTO reservations(start_date, end_date, user_id, room_id)
                VALUES(?, ?, ?, ?) RETURNING id
                """;

        UUID id = jdbcTemplate.queryForObject(
                insertReservation,
                UUID.class,
                entity.getStartDate(),
                entity.getEndDate(),
                entity.getUserId(),
                entity.getRoomId()
        );

        entity.setId(id);

        return entity;
    }

    @Override
    public Optional<Reservation> findById(UUID id) {
        String findReservation = "SELECT * FROM reservations WHERE id = ?";

        Optional<Reservation> reservation = Optional.of(
                jdbcTemplate.query(findReservation, (rs, rowNum) -> Reservation.builder()
                                .id(rs.getObject("id", UUID.class))
                                .startDate(rs.getObject("start_date", LocalDate.class))
                                .endDate(rs.getObject("end_date", LocalDate.class))
                                .roomId(rs.getObject("room_id", UUID.class))
                                .userId(rs.getObject("user_id", UUID.class))
                                .build())
                        .stream()
                        .findFirst()
                        .orElseThrow()
        );

        return reservation;
    }

    @Override
    public void delete(UUID id) {
        String deleteRelations = "DELETE FROM guest_to_reservation WHERE reservation_id = ?";
        jdbcTemplate.update(deleteRelations, id);

        String deleteReservation = "DELETE FROM reservations WHERE id = ?";
        jdbcTemplate.update(deleteReservation, id);
    }

    @Override
    public List<Reservation> findAll() {
        String findReservations = "SELECT * FROM reservations WHERE id = ?";

        List<Reservation> reservations =
                jdbcTemplate.query(findReservations, (rs, rowNum) -> Reservation.builder()
                                .id(rs.getObject("id", UUID.class))
                                .startDate(rs.getObject("start_date", LocalDate.class))
                                .endDate(rs.getObject("end_date", LocalDate.class))
                                .roomId(rs.getObject("room_id", UUID.class))
                                .userId(rs.getObject("user_id", UUID.class))
                                .build());

        return reservations;
    }

    @Override
    public Long count() {
        String getCount = "SELECT COUNT(*) FROM reservations";

        Long count = jdbcTemplate.queryForObject(getCount, Long.class);

        return count;
    }
}
