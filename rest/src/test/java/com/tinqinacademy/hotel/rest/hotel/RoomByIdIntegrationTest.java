package com.tinqinacademy.hotel.rest.hotel;

import com.tinqinacademy.hotel.api.operations.base.HotelMappings;
import com.tinqinacademy.hotel.persistence.entities.Room;
import com.tinqinacademy.hotel.persistence.models.BathroomType;
import com.tinqinacademy.hotel.persistence.repositories.RoomRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.convert.ConversionService;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class RoomByIdIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private ConversionService conversionService;

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
    public void testRoomById_success() throws Exception {
        mockMvc.perform(get(HotelMappings.GET_ROOM, savedRoom.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedRoom.getId().toString()))
                .andExpect(jsonPath("$.floor").value(savedRoom.getFloor()))
                .andExpect(jsonPath("$.price").value(savedRoom.getPrice().doubleValue()));
    }

    @Test
    public void testRoomById_notFound_failure() throws Exception {
        String roomId = UUID.randomUUID().toString();

        mockMvc.perform(get(HotelMappings.GET_ROOM, roomId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.errors[0].message").value(String.format("Room with id %s not found", roomId)));
    }
}
