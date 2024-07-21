package com.tinqinacademy.hotel.persistence.repositories;

import com.tinqinacademy.hotel.persistence.entity.Guest;
import com.tinqinacademy.hotel.persistence.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class UserRepositoryImpl implements UserRepository {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User save(User entity) {
        String saveUser = """
                INSERT INTO users(username, password, birth_date, phone_number, email)
                VALUES(?, ?, ?, ?, ?) RETURNING ID
                """;

        UUID id = jdbcTemplate.queryForObject(
                saveUser,
                UUID.class,
                entity.getUsername(),
                entity.getPassword(),
                entity.getBirthDate(),
                entity.getPhoneNumber(),
                entity.getEmail()
        );

        entity.setId(id);

        return entity;
    }

    @Override
    public Optional<User> findById(UUID id) {
        String findUser = "SELECT * FROM users WHERE id = ?";

        Optional<User> user = Optional.of(
                jdbcTemplate.query(findUser, (rs, rowNum) -> User.builder()
                                .id(UUID.fromString(rs.getString("id")))
                                .username(rs.getString("username"))
                                .password(rs.getString("password"))
                                .birthDate(rs.getDate("birth_date"))
                                .phoneNumber(rs.getString("phone_number"))
                                .email(rs.getString("email"))
                                .build(), id)
                        .stream()
                        .findFirst()
                        .orElseThrow()
        );

        return user;
    }

    @Override
    public void delete(UUID id) {
        String deleteReservations = "DELETE FROM reservations WHERE user_id = ?";
        jdbcTemplate.update(deleteReservations, id);

        String deleteUser = "DELETE FROM users WHERE id = ?";
        jdbcTemplate.update(deleteUser, id);
    }

    @Override
    public List<User> findAll() {
        String findUser = "SELECT * FROM users";

        List<User> users = jdbcTemplate.query(findUser, (rs, rowNum) -> User.builder()
                .id(UUID.fromString(rs.getString("id")))
                .username(rs.getString("username"))
                .password(rs.getString("password"))
                .birthDate(rs.getDate("birth_date"))
                .phoneNumber(rs.getString("phone_number"))
                .email(rs.getString("email"))
                .build());

        return users;
    }

    @Override
    public Long count() {
        String getCount = "SELECT COUNT(*) FROM users";

        Long count = jdbcTemplate.queryForObject(getCount, Long.class);

        return count;
    }
}
