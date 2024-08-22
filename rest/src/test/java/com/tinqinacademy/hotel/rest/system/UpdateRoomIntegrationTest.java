package com.tinqinacademy.hotel.rest.system;

import com.tinqinacademy.hotel.api.operations.base.HotelMappings;
import com.tinqinacademy.hotel.persistence.entities.Bed;
import com.tinqinacademy.hotel.persistence.entities.Room;
import com.tinqinacademy.hotel.persistence.models.BathroomType;
import com.tinqinacademy.hotel.persistence.models.BedSize;
import com.tinqinacademy.hotel.persistence.repositories.BedRepository;
import com.tinqinacademy.hotel.persistence.repositories.RoomRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class UpdateRoomIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RoomRepository roomRepository;

    @MockBean
    private BedRepository bedRepository;

    @AfterEach
    public void cleanRooms() {
        this.roomRepository.deleteAll();
    }

    @Test
    public void testUpdateRoom_success() throws Exception {
        String input = """
                 {
                    "bedSizes": [
                      "single"
                    ],
                    "bathRoomType": "private",
                    "roomNumber": "test",
                    "price": 123
                  }
                """;

        Bed bed = Bed.builder()
                .bedSize(BedSize.SINGLE)
                .count(1)
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

        when(roomRepository.findById(any(UUID.class)))
                .thenReturn(Optional.of(room));

        when(bedRepository.findAllByBedSizeIn(anyList()))
                .thenReturn(List.of(bed));

        when(roomRepository.save(any(Room.class)))
                .thenReturn(room);

        mockMvc.perform(put(HotelMappings.UPDATE_ROOM, roomId.toString())
                        .content(input)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(roomId.toString()));
    }

    @Test
    public void testUpdateRoom_roomNotFound_failure() throws Exception {
        String input = """
                 {
                    "bedSizes": [
                      "single"
                    ],
                    "bathRoomType": "private",
                    "roomNumber": "test",
                    "price": 123
                  }
                """;

        UUID roomId = UUID.randomUUID();
        when(roomRepository.findById(any(UUID.class)))
                .thenReturn(Optional.empty());

        mockMvc.perform(put(HotelMappings.UPDATE_ROOM, roomId.toString())
                        .content(input)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
