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

    @MockBean
    private RoomRepository roomRepository;

    @MockBean
    private BedRepository bedRepository;

    @AfterEach
    public void cleanRooms() {
        this.roomRepository.deleteAll();
    }

    @Test
    public void testCreateRoom_success() throws Exception {
        String input = """
                {
                  "bedSizes": [
                    "single"
                  ],
                  "bathRoomType": "private",
                  "floor": 20,
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

        when(roomRepository.existsByRoomNumber(any(String.class)))
                .thenReturn(false);

        when(bedRepository.findAllByBedSizeIn(anyList()))
                .thenReturn(List.of(bed));

        when(roomRepository.save(any(Room.class)))
                .thenReturn(room);


        mockMvc.perform(post(HotelMappings.CREATE_ROOM)
                        .content(input)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(roomId.toString()));
    }

    @Test
    public void testCreateRoom_roomNotAvailable_failure() throws Exception {
        String input = """
                {
                  "bedSizes": [
                    "single"
                  ],
                  "bathRoomType": "private",
                  "floor": 20,
                  "roomNumber": "test",
                  "price": 123
                }
                """;

        when(roomRepository.existsByRoomNumber(any(String.class)))
                .thenReturn(true);

        mockMvc.perform(post(HotelMappings.CREATE_ROOM)
                        .content(input)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value(409))
                .andExpect(jsonPath("$.errors[0].message").value("Room with room number test already exists"));
    }
}
