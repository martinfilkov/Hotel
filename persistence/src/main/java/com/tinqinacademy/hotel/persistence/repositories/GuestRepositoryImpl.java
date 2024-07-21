package com.tinqinacademy.hotel.persistence.repositories;

import com.tinqinacademy.hotel.persistence.entity.Guest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class GuestRepositoryImpl implements GuestRepository {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public GuestRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Guest save(Guest entity) {
        String saveGuest = """
                INSERT INTO guests(first_name,
                                   last_name,
                                   phone_number,
                                   id_card_validity,
                                   id_card_issue_authority,
                                   id_card_issue_date,
                                   birth_date)
                VALUES(?, ?, ?, ?, ?, ?, ?) RETURNING id
                """;

        UUID id = jdbcTemplate.queryForObject(
                saveGuest,
                UUID.class,
                entity.getFirstName(),
                entity.getLastName(),
                entity.getPhoneNumber(),
                entity.getIdCardValidity(),
                entity.getIdCardIssueAuthority(),
                entity.getIdCardIssueDate(),
                entity.getBirthDate()
        );

        entity.setId(id);

        return entity;
    }

    @Override
    public Optional<Guest> findById(UUID id) {
        String findGuest = "SELECT * FROM guests WHERE id = ?";

        Optional<Guest> guest = Optional.of(
                jdbcTemplate.query(findGuest, (rs, rowNum) -> Guest.builder()
                                .id(rs.getObject("id", UUID.class))
                                .firstName(rs.getString("first_name"))
                                .lastName(rs.getString("last_name"))
                                .phoneNumber(rs.getString("phone_number"))
                                .idCardValidity(rs.getString("id_card_validity"))
                                .idCardIssueAuthority(rs.getString("id_card_issue_authority"))
                                .idCardIssueDate(rs.getString("id_card_issue_date"))
                                .birthDate(rs.getObject("birth_date", LocalDate.class))
                                .build(), id)
                        .stream()
                        .findFirst()
                        .orElseThrow()
        );

        return guest;
    }

    @Override
    public void delete(UUID id) {
        String deleteRelation = "DELETE FROM guest_to_reservation WHERE guest_id = ?";
        jdbcTemplate.update(deleteRelation, id);

        String deleteGuest = "DELETE FROM guests WHERE id = ?";
        jdbcTemplate.update(deleteGuest, id);
    }

    @Override
    public List<Guest> findAll() {
        String findGuests = "SELECT * FROM guests";

        List<Guest> guests = jdbcTemplate.query(findGuests, (rs, rowNum) -> Guest.builder()
                .id(rs.getObject("id", UUID.class))
                .firstName(rs.getString("first_name"))
                .lastName(rs.getString("last_name"))
                .phoneNumber(rs.getString("phone_number"))
                .idCardValidity(rs.getString("id_card_validity"))
                .idCardIssueAuthority(rs.getString("id_card_issue_authority"))
                .idCardIssueDate(rs.getString("id_card_issue_date"))
                .birthDate(rs.getObject("birth_date", LocalDate.class))
                .build());

        return guests;
    }

    @Override
    public Long count() {
        String getCount = "SELECT COUNT(*) FROM guests";

        Long count = jdbcTemplate.queryForObject(getCount, Long.class);

        return count;
    }
}
