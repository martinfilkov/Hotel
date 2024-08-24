package com.tinqinacademy.hotel.rest.hotel;

import com.tinqinacademy.hotel.api.operations.base.HotelMappings;
import com.tinqinacademy.hotel.api.operations.hotel.getroomids.GetRoomIdsInput;
import com.tinqinacademy.hotel.persistence.entities.Room;
import com.tinqinacademy.hotel.persistence.models.BathroomType;
import com.tinqinacademy.hotel.persistence.repositories.RoomRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class GetRoomIdsIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RoomRepository roomRepository;

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
        savedRoom = roomRepository.save(room);
    }

    @AfterEach
    public void cleanRooms() {
        this.roomRepository.deleteAll();
    }

    @Test
    public void testGetRoomIds_room_exists_success() throws Exception {
        LocalDate startDate = LocalDate.now().plusDays(10);
        LocalDate endDate = LocalDate.now().plusDays(15);
        GetRoomIdsInput input = GetRoomIdsInput.builder()
                .startDate(startDate)
                .endDate(endDate)
                .bathroomType(Optional.empty())
                .bedSize(Optional.empty())
                .build();

        mockMvc.perform(get(HotelMappings.GET_IDS)
                        .param("startDate", input.getStartDate().toString())
                        .param("endDate", input.getEndDate().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ids").isArray())
                .andExpect(jsonPath("$.ids.size()").value(1))
                .andExpect(jsonPath("$.ids[0]").value(savedRoom.getId().toString()));
    }

    @Test
    public void testGetRoomIds_invalid_dates_failure() throws Exception {
        LocalDate startDate = LocalDate.now().plusDays(15);
        LocalDate endDate = LocalDate.now().plusDays(10);
        GetRoomIdsInput input = GetRoomIdsInput.builder()
                .startDate(startDate)
                .endDate(endDate)
                .bathroomType(Optional.empty())
                .bedSize(Optional.empty())
                .build();

        mockMvc.perform(get(HotelMappings.GET_IDS)
                        .param("startDate", input.getStartDate().toString())
                        .param("endDate", input.getEndDate().toString()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.errors[0].message").value("Start date cannot be after end date"));
    }
}
