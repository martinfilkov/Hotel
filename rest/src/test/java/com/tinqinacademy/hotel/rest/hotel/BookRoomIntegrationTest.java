package com.tinqinacademy.hotel.rest.hotel;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tinqinacademy.hotel.api.operations.base.HotelMappings;
import com.tinqinacademy.hotel.api.operations.hotel.bookroom.BookRoomInput;
import com.tinqinacademy.hotel.persistence.entities.Reservation;
import com.tinqinacademy.hotel.persistence.entities.Room;
import com.tinqinacademy.hotel.persistence.models.BathroomType;
import com.tinqinacademy.hotel.persistence.repositories.ReservationRepository;
import com.tinqinacademy.hotel.persistence.repositories.RoomRepository;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class BookRoomIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private RoomRepository roomRepository;

    private Room savedRoom;

    @BeforeEach
    public void createRoom() {
        Room room = Room.builder()
                .bathroomType(BathroomType.PRIVATE)
                .roomNumber("test")
                .bedSizes(List.of())
                .price(BigDecimal.TEN)
                .floor(3)
                .build();
        savedRoom = roomRepository.save(room);
    }

    @AfterEach
    public void cleanRoomsAndReservations() {
        this.roomRepository.deleteAll();
        this.reservationRepository.deleteAll();
    }

    @Test
    @Transactional
    public void testBookRoom_returns_200_when_startDateValid_and_endDateValid() throws Exception {
        String firstName = "Test";
        String lastName = "Testov";
        String phone = "0888888888";
        LocalDate startDate = LocalDate.now().plusDays(10);
        LocalDate endDate = LocalDate.now().plusDays(15);
        UUID userId = UUID.randomUUID();

        BookRoomInput bookInput = BookRoomInput.builder()
                .firstName(firstName)
                .lastName(lastName)
                .phone(phone)
                .startDate(startDate)
                .endDate(endDate)
                .userId(userId.toString())
                .build();

        String input = objectMapper.writeValueAsString(bookInput);

        MvcResult mvcResult = mockMvc.perform(post(HotelMappings.BOOK_ROOM, savedRoom.getId().toString())
                        .content(input)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        Reservation first = reservationRepository.findAll().getFirst();

        Assertions.assertEquals(1, reservationRepository.count());
        Assertions.assertEquals(startDate, first.getStartDate());
        Assertions.assertEquals(endDate, first.getEndDate());
    }

    @Test
    @Transactional
    public void testBookRoom_reservation_exists_failure() throws Exception {
        String firstName = "Test";
        String lastName = "Testov";
        String phone = "0888888888";
        LocalDate startDate = LocalDate.now().plusDays(10);
        LocalDate endDate = LocalDate.now().plusDays(15);
        UUID userId = UUID.randomUUID();

        BookRoomInput bookInput = BookRoomInput.builder()
                .firstName(firstName)
                .lastName(lastName)
                .phone(phone)
                .startDate(startDate)
                .endDate(endDate)
                .userId(userId.toString())
                .build();

        String input = objectMapper.writeValueAsString(bookInput);

        Reservation reservation = Reservation.builder()
                .room(savedRoom)
                .guests(List.of())
                .startDate(startDate)
                .endDate(endDate)
                .createdOn(LocalDateTime.now())
                .updatedOn(LocalDateTime.now())
                .userId(userId)
                .build();
        reservationRepository.save(reservation);

        mockMvc.perform(post(HotelMappings.BOOK_ROOM, savedRoom.getId().toString())
                        .content(input)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value(409))
                .andExpect(jsonPath("$.errors[0].message")
                        .value(String.format("Room with id %s is not available within the given period", savedRoom.getId().toString())));
    }


    @SneakyThrows
    @ParameterizedTest
    @ValueSource(strings = {"", "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa", "123"})
    public void returns400_whenFirstName_invalid(String firstName) {
        String lastName = "Testov";
        String phone = "0888888888";
        LocalDate startDate = LocalDate.now().plusDays(10);
        LocalDate endDate = LocalDate.now().plusDays(15);
        UUID userId = UUID.randomUUID();
        UUID roomId = UUID.randomUUID();

        BookRoomInput bookInput = BookRoomInput.builder()
                .firstName(firstName)
                .lastName(lastName)
                .phone(phone)
                .startDate(startDate)
                .endDate(endDate)
                .roomId(roomId.toString())
                .userId(userId.toString())
                .build();

        String input = objectMapper.writeValueAsString(bookInput);

        mockMvc.perform(MockMvcRequestBuilders
                        .post(HotelMappings.BOOK_ROOM, roomId.toString())
                        .content(input)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
