package com.tinqinacademy.hotel.rest.system;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tinqinacademy.hotel.api.operations.base.HotelMappings;
import com.tinqinacademy.hotel.api.operations.system.createroom.CreateRoomInput;
import com.tinqinacademy.hotel.persistence.entities.Bed;
import com.tinqinacademy.hotel.persistence.entities.Room;
import com.tinqinacademy.hotel.persistence.models.BathroomType;
import com.tinqinacademy.hotel.persistence.models.BedSize;
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
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.nio.channels.Pipe;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class CreateRoomIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private BedRepository bedRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @AfterEach
    public void cleanRooms() {
        this.bedRepository.deleteAll();
        this.roomRepository.deleteAll();
    }

    @Test
    @Transactional
    public void testCreateRoom_success() throws Exception {
        List<String> bedSizes = List.of("single");
        String bathroomType = "private";
        Integer floor = 20;
        String roomNumber = "test";
        BigDecimal price = BigDecimal.valueOf(123);

        CreateRoomInput createInput = CreateRoomInput.builder()
                .bedSizes(bedSizes)
                .bathRoomType(bathroomType)
                .floor(floor)
                .roomNumber(roomNumber)
                .price(price)
                .build();

        String input = objectMapper.writeValueAsString(createInput);

        mockMvc.perform(post(HotelMappings.CREATE_ROOM)
                        .content(input)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    @Transactional
    public void return409_roomNotAvailable_failure() throws Exception {
        List<String> bedSizes = List.of("single");
        String bathroomType = "private";
        Integer floor = 20;
        String roomNumber = "test";
        BigDecimal price = BigDecimal.valueOf(123);

        CreateRoomInput createInput = CreateRoomInput.builder()
                .bedSizes(bedSizes)
                .bathRoomType(bathroomType)
                .floor(floor)
                .roomNumber(roomNumber)
                .price(price)
                .build();

        String input = objectMapper.writeValueAsString(createInput);

        Room room = Room.builder()
                .bedSizes(List.of())
                .bathroomType(BathroomType.PRIVATE)
                .roomNumber(roomNumber)
                .createdOn(LocalDateTime.now())
                .updatedOn(LocalDateTime.now())
                .price(price)
                .floor(floor)
                .build();

        roomRepository.save(room);

        mockMvc.perform(post(HotelMappings.CREATE_ROOM)
                        .content(input)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value(409))
                .andExpect(jsonPath("$.errors[0].message").value("Room with room number test already exists"));
    }

    @SneakyThrows
    @ParameterizedTest
    @ValueSource(ints = {-123, 21, -1, 0})
    public void return400_when_floor_not_valid(Integer floor) {
        List<String> bedSizes = List.of("single");
        String bathroomType = "private";
        String roomNumber = "test";
        BigDecimal price = BigDecimal.valueOf(123);

        CreateRoomInput createInput = CreateRoomInput.builder()
                .bedSizes(bedSizes)
                .bathRoomType(bathroomType)
                .floor(floor)
                .roomNumber(roomNumber)
                .price(price)
                .build();

        String input = objectMapper.writeValueAsString(createInput);

        mockMvc.perform(post(HotelMappings.CREATE_ROOM)
                        .content(input)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
