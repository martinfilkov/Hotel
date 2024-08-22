package com.tinqinacademy.hotel.rest.system;

import com.tinqinacademy.hotel.api.operations.base.HotelMappings;
import com.tinqinacademy.hotel.persistence.entities.Reservation;
import com.tinqinacademy.hotel.persistence.repositories.GuestRepository;
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

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class RegisterVisitorIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReservationRepository reservationRepository;

    @MockBean
    private RoomRepository roomRepository;

    @MockBean
    private GuestRepository guestRepository;

    @AfterEach
    public void cleanRoomsAndReservationsAndGuest() {
        this.roomRepository.deleteAll();
        this.guestRepository.deleteAll();
        this.reservationRepository.deleteAll();
    }

    @Test
    public void testRegisterVisitor_success() throws Exception {
        String input = """
                {
                  "startDate": "2100-08-21",
                  "endDate": "2100-08-21",
                  "roomNumber": "string",
                  "visitors": [
                    {
                      "firstName": "string",
                      "lastName": "string",
                      "phone": "stringstri",
                      "idCardNumber": "string",
                      "idCardValidity": "string",
                      "idCardIssueAuthority": "string",
                      "idCardIssueDate": "string",
                      "birthDate": "2024-08-21"
                    }
                  ]
                }
                """;

        Reservation reservation = Reservation.builder()
                .id(UUID.randomUUID())
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(5))
                .userId(UUID.randomUUID())
                .guests(List.of())
                .build();

        when(roomRepository.existsByRoomNumber(any(String.class)))
                .thenReturn(true);

        when(reservationRepository.findAvailableRoomByRoomNumberAndPeriod(
                any(String.class),
                any(LocalDate.class),
                any(LocalDate.class)))
                .thenReturn(Optional.of(reservation));

        mockMvc.perform(post(HotelMappings.REGISTER_VISITOR)
                        .content(input)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    public void testRegisterVisitor_roomNotFound_failure() throws Exception {
        String input = """
                {
                  "startDate": "2100-08-21",
                  "endDate": "2100-08-21",
                  "roomNumber": "string",
                  "visitors": [
                    {
                      "firstName": "string",
                      "lastName": "string",
                      "phone": "stringstri",
                      "idCardNumber": "string",
                      "idCardValidity": "string",
                      "idCardIssueAuthority": "string",
                      "idCardIssueDate": "string",
                      "birthDate": "2024-08-21"
                    }
                  ]
                }
                """;

        when(roomRepository.existsByRoomNumber(any(String.class)))
                .thenReturn(true);

        mockMvc.perform(post(HotelMappings.REGISTER_VISITOR)
                        .content(input)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
    }
}
