package com.tinqinacademy.hotel.persistence.repositories;

import com.tinqinacademy.hotel.persistence.entity.Room;
import com.tinqinacademy.hotel.persistence.model.BathroomType;
import com.tinqinacademy.hotel.persistence.model.BedSize;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class RoomRepositoryImpl implements RoomRepository {
    private final JdbcTemplate jdbcTemplate;

    public RoomRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Room save(Room entity) {
        String roomInsert = "INSERT INTO rooms(bathroom_type, floor, room_number, price)" +
                "VALUES(?, ?, ?, ?) RETURNING id";
        UUID roomId = jdbcTemplate.queryForObject(roomInsert,
                UUID.class,
                entity.getBathroomType().toString(),
                entity.getFloor(),
                entity.getRoomNumber(),
                entity.getPrice()
        );
        entity.setId(roomId);

        String getBedIdQuery = "SELECT id FROM beds WHERE bed_size = ?";

        entity.getBedSizes().stream()
                .map(bedSize -> jdbcTemplate.queryForObject(getBedIdQuery, Long.class, bedSize.toString()))
                .forEach(bedId -> {
                    String roomToBedInsert = "INSERT INTO room_to_bed(room_id, bed_id) VALUES(?, ?)";
                    jdbcTemplate.update(roomToBedInsert, roomId, bedId);
                });

        return entity;
    }

    @Override
    public Optional<Room> findById(UUID uuid) {
        String findQuery = """
                SELECT
                    r.id,
                    r.bathroom_type,
                    r.floor,
                    r.room_number,
                    r.price,
                    string_agg(b.bed_size, ', ') AS bed_sizes
                FROM
                    rooms r
                JOIN
                    room_to_bed rb ON r.id = rb.room_id 
                JOIN
                    beds b ON rb.bed_id = b.id 
                WHERE
                    r.id = ?
                GROUP BY
                    r.id, r.bathroom_type, r.floor, r.room_number, r.price;
                """;

        Optional<Room> room = Optional.of(
                jdbcTemplate.query(findQuery, (rs, rowNum) -> {
                            List<BedSize> bedSizes = Arrays.stream(rs.getString("bed_sizes").split(", "))
                                    .map(BedSize::getByCode)
                                    .toList();
                            return Room.builder()
                                    .id(rs.getObject("id", UUID.class))
                                    .bathroomType(BathroomType.getByCode(rs.getString("bathroom_type")))
                                    .floor(rs.getInt("floor"))
                                    .roomNumber(rs.getString("room_number"))
                                    .price(rs.getBigDecimal("price"))
                                    .bedSizes(bedSizes)
                                    .build();
                        }, uuid)
                        .stream()
                        .findFirst()
                        .orElseThrow()
        );

        return room;
    }

    @Override
    public void delete(UUID id) {
        String deleteRelations = "DELETE FROM room_to_bed WHERE room_id = ?";
        jdbcTemplate.update(deleteRelations, id);

        String deleteRoom = "DELETE FROM rooms WHERE id = ?";
        jdbcTemplate.update(deleteRoom, id);
    }

    @Override
    public List<Room> findAll() {
        String findQuery = """
                SELECT
                    r.id,
                    r.bathroom_type,
                    r.floor,
                    r.room_number,
                    r.price,
                    string_agg(b.bed_size, ', ') AS bed_sizes
                FROM
                    rooms r
                JOIN
                    room_to_bed rb ON r.id = rb.room_id 
                JOIN
                    beds b ON rb.bed_id = b.id 
                GROUP BY
                    r.id, r.bathroom_type, r.floor, r.room_number, r.price;
                """;

        List<Room> rooms =
                jdbcTemplate.query(findQuery, (rs, rowNum) -> {
                            List<BedSize> bedSizes = Arrays.stream(rs.getString("bed_sizes").split(", "))
                                    .map(BedSize::getByCode)
                                    .toList();
                            return Room.builder()
                                    .id(rs.getObject("id", UUID.class))
                                    .bathroomType(BathroomType.getByCode(rs.getString("bathroom_type")))
                                    .floor(rs.getInt("floor"))
                                    .roomNumber(rs.getString("room_number"))
                                    .price(rs.getBigDecimal("price"))
                                    .bedSizes(bedSizes)
                                    .build();
                        });

        return rooms;
    }

    @Override
    public Long count() {
        String getCount = "SELECT COUNT(*) FROM rooms";
        return jdbcTemplate.queryForObject(getCount, Long.class);
    }

    @Override
    public Room update(Room entity) {
        String roomUpdate = "UPDATE rooms SET bathroom_type = ?, room_number = ?, price = ? WHERE id = ?";
        jdbcTemplate.update(roomUpdate,
                entity.getBathroomType().toString(),
                entity.getRoomNumber(),
                entity.getPrice(),
                entity.getId());

        String deleteRoomToBed = "DELETE FROM room_to_bed WHERE room_id = ?";
        jdbcTemplate.update(deleteRoomToBed, entity.getId());

        String getBedIdQuery = "SELECT id FROM beds WHERE bed_size = ?";

        entity.getBedSizes().stream()
                .map(bedSize -> jdbcTemplate.queryForObject(getBedIdQuery, Long.class, bedSize.toString()))
                .forEach(bedId -> {
                    String roomToBedInsert = "INSERT INTO room_to_bed(room_id, bed_id) VALUES(?, ?)";
                    jdbcTemplate.update(roomToBedInsert, entity.getId(), bedId);
                });

        return entity;
    }
}
