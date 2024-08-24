package com.tinqinacademy.hotel.rest.system;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tinqinacademy.hotel.api.operations.base.HotelMappings;
import com.tinqinacademy.hotel.api.operations.system.registervisitor.RegisterVisitorInput;
import com.tinqinacademy.hotel.api.operations.system.registervisitor.RegisterVisitorInputList;
import com.tinqinacademy.hotel.persistence.entities.Guest;
import com.tinqinacademy.hotel.persistence.entities.Reservation;
import com.tinqinacademy.hotel.persistence.entities.Room;
import com.tinqinacademy.hotel.persistence.models.BathroomType;
import com.tinqinacademy.hotel.persistence.repositories.GuestRepository;
import com.tinqinacademy.hotel.persistence.repositories.ReservationRepository;
import com.tinqinacademy.hotel.persistence.repositories.RoomRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cglib.core.Local;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
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
public class RegisterVisitorIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private GuestRepository guestRepository;

    @Autowired
    private ObjectMapper objectMapper;

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
                .startDate(LocalDate.now().plusDays(10))
                .endDate(LocalDate.now().plusDays(15))
                .room(savedRoom)
                .userId(UUID.randomUUID())
                .guests(new ArrayList<>())
                .build();
        reservationRepository.save(reservation);
    }

    @AfterEach
    public void cleanRoomsAndReservationsAndGuest() {
        this.roomRepository.deleteAll();
        this.guestRepository.deleteAll();
        this.reservationRepository.deleteAll();
    }

    @Test
    @Transactional
    public void testRegisterVisitor_success() throws Exception {
        String firstName = "string";
        String lastName = "string";
        String phone = "0888888888";
        String idCardNumber = "string";
        String idCardValidity = "string";
        String idCardIssueAuthority = "string";
        String idCardIssueDate = "string";
        LocalDate birthDate = LocalDate.now().minusYears(20);
        RegisterVisitorInput guest = RegisterVisitorInput.builder()
                .firstName(firstName)
                .lastName(lastName)
                .phone(phone)
                .idCardNumber(idCardNumber)
                .idCardIssueAuthority(idCardIssueAuthority)
                .idCardValidity(idCardValidity)
                .idCardIssueDate(idCardIssueDate)
                .birthDate(birthDate)
                .build();

        LocalDate startDate = LocalDate.now().plusDays(10);
        LocalDate endDate = LocalDate.now().plusDays(15);
        String roomNumber = "test";
        RegisterVisitorInputList registerInput = RegisterVisitorInputList.builder()
                .startDate(startDate)
                .endDate(endDate)
                .roomNumber(roomNumber)
                .visitors(List.of(guest))
                .build();

        String input = objectMapper.writeValueAsString(registerInput);

        MvcResult mvcResult = mockMvc.perform(post(HotelMappings.REGISTER_VISITOR)
                        .content(input)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        Reservation first = reservationRepository.findAll().getFirst();
        Assertions.assertEquals(1, first.getGuests().size());
        Assertions.assertEquals(guest.getFirstName(), first.getGuests().getFirst().getFirstName());
    }

    @Test
    @Transactional
    public void testRegisterVisitor_roomNotFound_failure() throws Exception {
        String firstName = "string";
        String lastName = "string";
        String phone = "0888888888";
        String idCardNumber = "string";
        String idCardValidity = "string";
        String idCardIssueAuthority = "string";
        String idCardIssueDate = "string";
        LocalDate birthDate = LocalDate.now().minusYears(20);
        RegisterVisitorInput guest = RegisterVisitorInput.builder()
                .firstName(firstName)
                .lastName(lastName)
                .phone(phone)
                .idCardNumber(idCardNumber)
                .idCardIssueAuthority(idCardIssueAuthority)
                .idCardValidity(idCardValidity)
                .idCardIssueDate(idCardIssueDate)
                .birthDate(birthDate)
                .build();

        LocalDate startDate = LocalDate.now().plusDays(10);
        LocalDate endDate = LocalDate.now().plusDays(15);
        String roomNumber = "testWrong";
        RegisterVisitorInputList registerInput = RegisterVisitorInputList.builder()
                .startDate(startDate)
                .endDate(endDate)
                .roomNumber(roomNumber)
                .visitors(List.of(guest))
                .build();

        String input = objectMapper.writeValueAsString(registerInput);

        mockMvc.perform(post(HotelMappings.REGISTER_VISITOR)
                        .content(input)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.errors[0].message")
                        .value(String.format("Room with room number %s not found", roomNumber)));
    }
}
