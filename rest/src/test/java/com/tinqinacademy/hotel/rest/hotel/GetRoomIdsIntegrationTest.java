package com.tinqinacademy.hotel.rest.hotel;

import com.tinqinacademy.hotel.api.operations.base.HotelMappings;
import com.tinqinacademy.hotel.api.operations.hotel.getroomids.GetRoomIdsInput;
import com.tinqinacademy.hotel.api.operations.hotel.getroomids.GetRoomIdsOutput;
import com.tinqinacademy.hotel.persistence.entities.Bed;
import com.tinqinacademy.hotel.persistence.entities.Room;
import com.tinqinacademy.hotel.persistence.models.BathroomType;
import com.tinqinacademy.hotel.persistence.models.BedSize;
import com.tinqinacademy.hotel.persistence.repositories.RoomRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class GetRoomIdsIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RoomRepository roomRepository;

    @AfterEach
    public void cleanRooms() {
        roomRepository.deleteAll();
    }

    @Test
    public void testGetRoomIds_success() throws Exception {
        GetRoomIdsInput input = GetRoomIdsInput.builder()
                .startDate(LocalDate.of(2024, 9, 1))
                .endDate(LocalDate.of(2024, 9, 10))
                .bathroomType(Optional.empty())
                .bedSize(Optional.empty())
                .build();

        Bed bed = Bed.builder()
                .id(UUID.randomUUID())
                .bedSize(BedSize.DOUBLE)
                .build();

        UUID roomId = UUID.randomUUID();
        Room room = Room.builder()
                .id(roomId)
                .bathroomType(BathroomType.PRIVATE)
                .roomNumber("test")
                .bedSizes(List.of(bed))
                .price(BigDecimal.TEN)
                .floor(3)
                .build();

        when(roomRepository.findAvailableRooms(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(List.of(room));

        GetRoomIdsOutput expectedOutput = GetRoomIdsOutput.builder()
                .ids(List.of(room.getId().toString()))
                .build();

        mockMvc.perform(get(HotelMappings.GET_IDS)
                        .param("startDate", input.getStartDate().toString())
                        .param("endDate", input.getEndDate().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ids").isArray())
                .andExpect(jsonPath("$.ids.size()").value(1))
                .andExpect(jsonPath("$.ids[0]").value(expectedOutput.getIds().getFirst()));
    }

    @Test
    public void testGetRoomIds_invalid_dates_failure() throws Exception {
        GetRoomIdsInput input = GetRoomIdsInput.builder()
                .startDate(LocalDate.of(2024, 9, 10))
                .endDate(LocalDate.of(2024, 9, 1))
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
