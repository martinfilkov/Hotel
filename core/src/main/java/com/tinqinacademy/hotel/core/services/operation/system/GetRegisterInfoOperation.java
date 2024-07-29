package com.tinqinacademy.hotel.core.services.operation.system;

import com.tinqinacademy.hotel.api.operations.system.inforregister.GetRegisterInfoProcess;
import com.tinqinacademy.hotel.api.operations.system.inforregister.InfoRegisterInput;
import com.tinqinacademy.hotel.api.operations.system.inforregister.InfoRegisterOutput;
import com.tinqinacademy.hotel.api.operations.system.inforregister.InfoRegisterOutputList;
import com.tinqinacademy.hotel.core.utils.SpecificationUtils;
import com.tinqinacademy.hotel.persistence.entity.Guest;
import com.tinqinacademy.hotel.persistence.repositories.GuestRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.tinqinacademy.hotel.core.specifications.GuestSpecifications.*;

@Slf4j
@Service
public class GetRegisterInfoOperation implements GetRegisterInfoProcess {
    private final GuestRepository guestRepository;
    private final ConversionService conversionService;

    @Autowired
    public GetRegisterInfoOperation(GuestRepository guestRepository, ConversionService conversionService) {
        this.guestRepository = guestRepository;
        this.conversionService = conversionService;
    }

    @Override
    public InfoRegisterOutputList process(InfoRegisterInput input) {
        log.info("Start getRegisterInfo input: {}", input);

        List<Specification<Guest>> predicates = new ArrayList<>() {{
            add(guestHasFirstName(input.getFirstName()));
            add(guestHasLastName(input.getLastName()));
            add(guestHasPhoneNumber(input.getPhone()));
            add(guestHasIdCardNumber(input.getIdCardNumber()));
            add(guestHasIdCardValidity(input.getIdCardValidity()));
            add(guestHasIdCardIssueDate(input.getIdCardIssueDate()));
            add(guestHasIdCardIssueAuthority(input.getIdCardIssueAuthority()));
        }};

        Specification<Guest> specification = SpecificationUtils.combineSpecifications(predicates);
        List<Guest> specifiedGuests = guestRepository.findAll(specification);
        List<Guest> allGuests = guestRepository.findByDateRangeAndRoomNumber(
                input.getStartDate(), input.getEndDate(), input.getRoomNumber()
        );

        List<Guest> filteredGuests = allGuests.stream()
                .filter(specifiedGuests::contains)
                .toList();

        List<InfoRegisterOutput> guestInfo = filteredGuests.stream()
                .map(guest -> conversionService.convert(guest, InfoRegisterOutput.class))
                .toList();

        InfoRegisterOutputList output = InfoRegisterOutputList.builder()
                .visitors(guestInfo)
                .build();

        log.info("End getRegisterInfo output: {}", output);
        return output;
    }
}
