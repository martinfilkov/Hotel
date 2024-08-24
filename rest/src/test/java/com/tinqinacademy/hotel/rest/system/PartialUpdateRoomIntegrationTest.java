package com.tinqinacademy.hotel.rest.system;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tinqinacademy.hotel.api.operations.base.HotelMappings;
import com.tinqinacademy.hotel.api.operations.system.partialupdate.PartialUpdateRoomInput;
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

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class PartialUpdateRoomIntegrationTest {
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
    public void testPartialUpdateRoom_success() throws Exception {
        BigDecimal price = BigDecimal.valueOf(123);
        PartialUpdateRoomInput partialInput = PartialUpdateRoomInput.builder()
                .price(price)
                .build();

        String input = objectMapper.writeValueAsString(partialInput);

        mockMvc.perform(patch(HotelMappings.PARTIAL_UPDATE_ROOM, savedRoom.getId().toString())
                        .content(input)
                        .contentType("application/json-patch+json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedRoom.getId().toString()));
    }

    @Test
    public void testPartialUpdateRoom_roomNotFound_failure() throws Exception {
        BigDecimal price = BigDecimal.valueOf(123);
        PartialUpdateRoomInput partialInput = PartialUpdateRoomInput.builder()
                .price(price)
                .build();

        String input = objectMapper.writeValueAsString(partialInput);

        UUID roomId = UUID.randomUUID();

        mockMvc.perform(patch(HotelMappings.PARTIAL_UPDATE_ROOM, roomId.toString())
                        .content(input)
                        .contentType("application/json-patch+json"))
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @ParameterizedTest
    @ValueSource(strings = {"asjldfasdlfk", "123124124"})
    public void return400_when_price_is_invalid(String bathRoomType) {
        PartialUpdateRoomInput partialInput = PartialUpdateRoomInput.builder()
                .bathRoomType(bathRoomType)
                .build();

        String input = objectMapper.writeValueAsString(partialInput);

        mockMvc.perform(patch(HotelMappings.PARTIAL_UPDATE_ROOM, savedRoom.getId().toString())
                        .content(input)
                        .contentType("application/json-patch+json"))
                .andExpect(status().isBadRequest());
    }
}
