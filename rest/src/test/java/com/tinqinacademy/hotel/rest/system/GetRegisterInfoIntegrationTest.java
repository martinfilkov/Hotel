package com.tinqinacademy.hotel.rest.system;

import com.tinqinacademy.hotel.api.operations.base.HotelMappings;
import com.tinqinacademy.hotel.persistence.entities.Guest;
import com.tinqinacademy.hotel.persistence.repositories.GuestRepository;
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

    @MockBean
    private GuestRepository guestRepository;

    @AfterEach
    public void cleanGuests() {
        this.guestRepository.deleteAll();
    }

    @Test
    public void testGetRegisterInfo_success() throws Exception {
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
                .phoneNumber("08888888888")
                .build();

        when(guestRepository.findAll())
                .thenReturn(List.of(guest));

        when(guestRepository.findByDateRangeAndRoomNumber(any(LocalDate.class), any(LocalDate.class), any(String.class)))
                .thenReturn(List.of(guest));


        mockMvc.perform(get(HotelMappings.INFO_REGISTRY)
                        .param("startDate", LocalDate.of(2100, 9, 1).toString())
                        .param("endDate", LocalDate.of(2100, 9, 10).toString())
                        .param("roomNumber", "test"))
                .andExpect(status().isOk());
    }
}
