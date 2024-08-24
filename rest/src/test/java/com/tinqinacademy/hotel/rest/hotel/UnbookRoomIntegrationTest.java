package com.tinqinacademy.hotel.rest.hotel;

import com.tinqinacademy.hotel.api.operations.base.HotelMappings;
import com.tinqinacademy.hotel.persistence.entities.Reservation;
import com.tinqinacademy.hotel.persistence.entities.Room;
import com.tinqinacademy.hotel.persistence.models.BathroomType;
import com.tinqinacademy.hotel.persistence.repositories.ReservationRepository;
import com.tinqinacademy.hotel.persistence.repositories.RoomRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class UnbookRoomIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private RoomRepository roomRepository;

    private Reservation savedReservation;

    @BeforeEach
    public void createReservation() {
        Room room = Room.builder()
                .bathroomType(BathroomType.PRIVATE)
                .roomNumber("test")
                .bedSizes(List.of())
                .price(BigDecimal.TEN)
                .floor(3)
                .build();
        Room savedRoom = roomRepository.save(room);

        Reservation reservation = Reservation.builder()
                .id(UUID.randomUUID())
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(5))
                .room(savedRoom)
                .userId(UUID.randomUUID())
                .build();
        savedReservation = reservationRepository.save(reservation);
    }

    @AfterEach
    public void cleanReservations() {
        this.roomRepository.deleteAll();
        this.reservationRepository.deleteAll();
    }

    @Test
    @Transactional
    public void testUnbookRoom_unbook_reservation_success() throws Exception {
        mockMvc.perform(delete(HotelMappings.UNBOOK_ROOM, savedReservation.getId().toString()))
                .andExpect(status().isAccepted());
    }

    @Test
    @Transactional
    public void testUnbookRoom_reservationNotFound_failure() throws Exception {
        UUID reservationId = UUID.randomUUID();

        mockMvc.perform(delete(HotelMappings.UNBOOK_ROOM, reservationId))
                .andExpect(status().isNotFound());
    }
}
