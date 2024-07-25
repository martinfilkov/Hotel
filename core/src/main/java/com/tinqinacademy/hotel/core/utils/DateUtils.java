package com.tinqinacademy.hotel.core.utils;

import com.tinqinacademy.hotel.persistence.entity.Reservation;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DateUtils {
    public static List<LocalDate> getDatesOccupied(List<Reservation> reservations) {
        List<LocalDate> datesOccupied = new ArrayList<>();

        for (Reservation res : reservations) {
            LocalDate startDate = res.getStartDate();
            LocalDate endDate = res.getEndDate();
            while (!startDate.isAfter(endDate)) {
                datesOccupied.add(startDate);
                startDate = startDate.plusDays(1);
            }
        }
        return datesOccupied;
    }
}
