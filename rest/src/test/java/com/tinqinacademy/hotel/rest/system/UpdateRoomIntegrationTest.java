package com.tinqinacademy.hotel.rest.system;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tinqinacademy.hotel.api.operations.base.HotelMappings;
import com.tinqinacademy.hotel.api.operations.system.updateroom.UpdateRoomInput;
import com.tinqinacademy.hotel.persistence.entities.Room;
import com.tinqinacademy.hotel.persistence.models.BathroomType;
import com.tinqinacademy.hotel.persistence.repositories.BedRepository;
import com.tinqinacademy.hotel.persistence.repositories.RoomRepository;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class UpdateRoomIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private BedRepository bedRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Room savedRoom;

    @BeforeEach
    public void createRoom() {
        Room room = Room.builder()
                .bathroomType(BathroomType.PRIVATE)
                .roomNumber("test")
                .bedSizes(List.of())
                .price(BigDecimal.TEN)
                .floor(3)
                .build();
        savedRoom = this.roomRepository.save(room);
    }

    @AfterEach
    public void cleanRooms() {
        this.roomRepository.deleteAll();
    }

    @Test
    public void testUpdateRoom_success() throws Exception {
        List<String> bedSizes = List.of("single");
        String bathroomType = "private";
        String roomNumber = "test";
        BigDecimal price = BigDecimal.valueOf(123);

        UpdateRoomInput updateInput = UpdateRoomInput.builder()
                .bedSizes(bedSizes)
                .bathRoomType(bathroomType)
                .roomNumber(roomNumber)
                .price(price)
                .build();

        String input = objectMapper.writeValueAsString(updateInput);

        mockMvc.perform(put(HotelMappings.UPDATE_ROOM, savedRoom.getId().toString())
                        .content(input)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedRoom.getId().toString()));
    }

    @Test
    public void testUpdateRoom_roomNotFound_failure() throws Exception {
        List<String> bedSizes = List.of("single");
        String bathroomType = "private";
        String roomNumber = "test";
        BigDecimal price = BigDecimal.valueOf(123);

        UpdateRoomInput updateInput = UpdateRoomInput.builder()
                .bedSizes(bedSizes)
                .bathRoomType(bathroomType)
                .roomNumber(roomNumber)
                .price(price)
                .build();

        String input = objectMapper.writeValueAsString(updateInput);

        UUID roomId = UUID.randomUUID();

        mockMvc.perform(put(HotelMappings.UPDATE_ROOM, roomId.toString())
                        .content(input)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @ParameterizedTest
    @ValueSource(strings = {"asdasdasd", "123"})
    public void return_400_when_bathroomType_invalid(String bathroomType) {
        List<String> bedSizes = List.of("single");
        String roomNumber = "test";
        BigDecimal price = BigDecimal.valueOf(123);

        UpdateRoomInput updateInput = UpdateRoomInput.builder()
                .bedSizes(bedSizes)
                .bathRoomType(bathroomType)
                .roomNumber(roomNumber)
                .price(price)
                .build();

        String input = objectMapper.writeValueAsString(updateInput);

        mockMvc.perform(put(HotelMappings.UPDATE_ROOM, savedRoom.getId().toString())
                        .content(input)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

}
