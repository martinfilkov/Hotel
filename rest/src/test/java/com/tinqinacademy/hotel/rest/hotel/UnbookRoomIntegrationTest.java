package com.tinqinacademy.hotel.rest.hotel;

import com.tinqinacademy.hotel.api.operations.base.HotelMappings;
import com.tinqinacademy.hotel.persistence.entities.Reservation;
import com.tinqinacademy.hotel.persistence.repositories.ReservationRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
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

    @MockBean
    private ReservationRepository reservationRepository;

    @AfterEach
    public void cleanReservations() {
        this.reservationRepository.deleteAll();
    }

    @Test
    public void testUnbookRoom_success() throws Exception {
        Reservation reservation = Reservation.builder()
                .id(UUID.randomUUID())
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(5))
                .userId(UUID.randomUUID())
                .build();


        when(reservationRepository.findById(any(UUID.class)))
                .thenReturn(Optional.of(reservation));

        mockMvc.perform(delete(HotelMappings.UNBOOK_ROOM, reservation.getId().toString()))
                .andExpect(status().isAccepted());
    }

    @Test
    public void testUnbookRoom_reservationNotFound_failure() throws Exception {
        when(reservationRepository.findById(any(UUID.class)))
                .thenReturn(Optional.empty());

        mockMvc.perform(delete(HotelMappings.UNBOOK_ROOM, UUID.randomUUID().toString()))
                .andExpect(status().isNotFound());
    }
}
