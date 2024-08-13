package com.tinqinacademy.hotel.core.converters;

import com.tinqinacademy.hotel.api.operations.hotel.bookroom.BookRoomInput;
import com.tinqinacademy.hotel.persistence.entities.Reservation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class BookRoomInputToReservationConverter implements Converter<BookRoomInput, Reservation.ReservationBuilder> {
    @Override
    public Reservation.ReservationBuilder convert(BookRoomInput input) {
        log.info("Start converting from BookRoomInput to Reservation.Reservation with input: {}", input);

        Reservation.ReservationBuilder output = Reservation.builder()
                .startDate(input.getStartDate())
                .endDate(input.getEndDate());

        log.info("End converting from BookRoomInput to Reservation.Reservation with output: {}", output);
        return output;
    }
}
