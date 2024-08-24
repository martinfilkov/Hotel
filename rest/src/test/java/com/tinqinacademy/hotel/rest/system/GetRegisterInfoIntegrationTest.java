package com.tinqinacademy.hotel.rest.system;

import com.tinqinacademy.hotel.api.operations.base.HotelMappings;
import com.tinqinacademy.hotel.persistence.entities.Guest;
import com.tinqinacademy.hotel.persistence.entities.Reservation;
import com.tinqinacademy.hotel.persistence.entities.Room;
import com.tinqinacademy.hotel.persistence.models.BathroomType;
import com.tinqinacademy.hotel.persistence.repositories.GuestRepository;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class GetRegisterInfoIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private GuestRepository guestRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @BeforeEach
    public void createGuests() {
        Guest guest = Guest.builder()
                .id(UUID.randomUUID())
                .birthDate(LocalDate.now())
                .firstName("Martin")
                .lastName("Filkov")
                .createdOn(LocalDateTime.now())
                .updatedOn(LocalDateTime.now())
                .idCardIssueAuthority("test")
                .idCardIssueDate("test")
                .idCardValidity("test")
                .idCardIssueAuthority("test")
                .idCardNumber("test")
                .phoneNumber("08888888888")
                .build();
        guestRepository.save(guest);

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
                .startDate(LocalDate.now().plusDays(10))
                .endDate(LocalDate.now().plusDays(15))
                .room(savedRoom)
                .userId(UUID.randomUUID())
                .guests(List.of(guest))
                .build();
        reservationRepository.save(reservation);
    }

    @AfterEach
    public void cleanGuests() {
        this.guestRepository.deleteAll();
    }

    @Test
    @Transactional
    public void testGetRegisterInfo_success() throws Exception {
        mockMvc.perform(get(HotelMappings.INFO_REGISTRY)
                        .param("startDate", LocalDate.now().plusDays(10).toString())
                        .param("endDate", LocalDate.now().plusDays(15).toString())
                        .param("roomNumber", "test"))
                .andExpect(status().isOk());
    }
}
