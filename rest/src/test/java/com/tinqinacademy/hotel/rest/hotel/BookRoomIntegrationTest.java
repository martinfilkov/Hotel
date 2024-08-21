package com.tinqinacademy.hotel.rest.hotel;

import com.tinqinacademy.hotel.api.operations.base.HotelMappings;
import com.tinqinacademy.hotel.persistence.entities.Bed;
import com.tinqinacademy.hotel.persistence.entities.Reservation;
import com.tinqinacademy.hotel.persistence.entities.Room;
import com.tinqinacademy.hotel.persistence.models.BathroomType;
import com.tinqinacademy.hotel.persistence.models.BedSize;
import com.tinqinacademy.hotel.persistence.repositories.ReservationRepository;
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
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class BookRoomIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReservationRepository reservationRepository;

    @MockBean
    private RoomRepository roomRepository;

    @AfterEach
    public void cleanRoomsAndReservations() {
        this.roomRepository.deleteAll();
        this.reservationRepository.deleteAll();
    }

    @Test
    public void testBookRoom_success() throws Exception {
        when(reservationRepository.existsByRoomIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                any(UUID.class),
                any(LocalDate.class),
                any(LocalDate.class)))
                .thenReturn(false);

        UUID roomId = UUID.randomUUID();

        String input = """
                {
                    "firstName": "Test",
                    "lastName": "Testov",
                    "phone": "0888888888",
                    "startDate": "2100-10-10",
                    "endDate": "2100-10-15",
                    "userId": "00000000-0000-0000-0000-00000000000"
                }
                """;

        Bed bed = Bed.builder()
                .id(UUID.randomUUID())
                .bedSize(BedSize.DOUBLE)
                .build();

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

        Reservation reservation = Reservation.builder()
                .id(UUID.randomUUID())
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(5))
                .room(room)
                .userId(UUID.randomUUID())
                .build();

        when(reservationRepository.save(any(Reservation.class)))
                .thenReturn(reservation);

        mockMvc.perform(post(HotelMappings.BOOK_ROOM, roomId.toString())
                        .content(input)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    public void testBookRoom_reservation_exists_failure() throws Exception {
        when(reservationRepository.existsByRoomIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                any(UUID.class),
                any(LocalDate.class),
                any(LocalDate.class)))
                .thenReturn(true);

        UUID roomId = UUID.randomUUID();

        String input = """
                {
                    "firstName": "Test",
                    "lastName": "Testov",
                    "phone": "0888888888",
                    "startDate": "2100-10-10",
                    "endDate": "2100-10-15",
                    "userId": "00000000-0000-0000-0000-00000000000"
                }
                """;

        mockMvc.perform(post(HotelMappings.BOOK_ROOM, roomId.toString())
                        .content(input.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value(409))
                .andExpect(jsonPath("$.errors[0].message")
                        .value(String.format("Room with id %s is not available within the given period", roomId)))
        ;
    }
}
