package com.tinqinacademy.hotel.api.operations.hotel.getroomids;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDate;
import java.util.Optional;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class GetRoomIdsInput {
    @FutureOrPresent(message = "Date cannot be in the past")
    private LocalDate startDate;

    @FutureOrPresent(message = "Date cannot be in the past")
    private LocalDate endDate;

    private Optional<String> bedSize;

    private Optional<String> bathroomType;
}
