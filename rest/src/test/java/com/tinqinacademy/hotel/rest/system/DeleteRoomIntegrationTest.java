package com.tinqinacademy.hotel.rest.system;

import com.tinqinacademy.hotel.api.operations.base.HotelMappings;
import com.tinqinacademy.hotel.persistence.entities.Bed;
import com.tinqinacademy.hotel.persistence.entities.Room;
import com.tinqinacademy.hotel.persistence.models.BathroomType;
import com.tinqinacademy.hotel.persistence.models.BedSize;
import com.tinqinacademy.hotel.persistence.repositories.ReservationRepository;
import com.tinqinacademy.hotel.persistence.repositories.RoomRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import javax.annotation.meta.When;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class DeleteRoomIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RoomRepository roomRepository;

    @MockBean
    private ReservationRepository reservationRepository;

    @AfterEach
    public void cleanRooms() {
        this.roomRepository.deleteAll();
    }

    @Test
    public void testDeleteRoom_success() throws Exception {
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
                .price(BigDecimal.valueOf(123))
                .floor(3)
                .build();

        when(roomRepository.findById(any(UUID.class)))
                .thenReturn(Optional.of(room));

        when(reservationRepository.existsByRoomId(any(UUID.class)))
                .thenReturn(false);

        mockMvc.perform(delete(HotelMappings.DELETE_ROOM, roomId.toString()))
                .andExpect(status().isAccepted());
    }

    @Test
    public void testDeleteRoom_roomNotFound_failure() throws Exception {
        UUID roomId = UUID.randomUUID();
        when(roomRepository.findById(any(UUID.class)))
                .thenReturn(Optional.empty());

        mockMvc.perform(delete(HotelMappings.DELETE_ROOM, roomId.toString()))
                .andExpect(status().isNotFound());
    }
}
