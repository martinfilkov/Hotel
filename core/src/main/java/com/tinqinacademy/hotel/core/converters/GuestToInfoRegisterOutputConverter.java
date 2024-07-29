package com.tinqinacademy.hotel.core.converters;

import com.tinqinacademy.hotel.api.operations.system.inforregister.InfoRegisterOutput;
import com.tinqinacademy.hotel.persistence.entity.Guest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class GuestToInfoRegisterOutputConverter implements Converter<Guest, InfoRegisterOutput> {
    @Override
    public InfoRegisterOutput convert(Guest guest) {
        return InfoRegisterOutput.builder()
                .firstName(guest.getFirstName())
                .lastName(guest.getLastName())
                .phone(guest.getPhoneNumber())
                .idCardNumber(guest.getIdCardNumber())
                .idCardIssueDate(guest.getIdCardIssueDate())
                .idCardIssueAuthority(guest.getIdCardIssueAuthority())
                .idCardValidity(guest.getIdCardValidity())
                .build();
    }
}
