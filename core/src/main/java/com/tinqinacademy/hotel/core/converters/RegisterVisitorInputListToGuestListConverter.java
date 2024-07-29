package com.tinqinacademy.hotel.core.converters;

import com.tinqinacademy.hotel.api.operations.system.registervisitor.RegisterVisitorInput;
import com.tinqinacademy.hotel.persistence.entity.Guest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RegisterVisitorInputListToGuestListConverter implements Converter<RegisterVisitorInput, Guest> {
    @Override
    public Guest convert(RegisterVisitorInput input) {
        log.info("Start converting from RegisterVisitorInput to Guest with input: {}", input);

        Guest output = Guest.builder()
                .firstName(input.getFirstName())
                .lastName(input.getLastName())
                .phoneNumber(input.getPhone())
                .birthDate(input.getBirthDate())
                .idCardNumber(input.getIdCardNumber())
                .idCardValidity(input.getIdCardValidity())
                .idCardIssueDate(input.getIdCardIssueDate())
                .idCardIssueAuthority(input.getIdCardIssueAuthority())
                .build();

        log.info("End converting from RegisterVisitorInput to Guest with output: {}", output);
        return output;
    }
}
