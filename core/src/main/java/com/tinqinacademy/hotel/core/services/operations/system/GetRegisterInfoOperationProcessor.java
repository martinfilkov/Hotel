package com.tinqinacademy.hotel.core.services.operations.system;

import com.tinqinacademy.hotel.api.operations.base.Errors;
import com.tinqinacademy.hotel.api.operations.exceptions.NotFoundException;
import com.tinqinacademy.hotel.api.operations.system.inforregister.GetRegisterInfoOperation;
import com.tinqinacademy.hotel.api.operations.system.inforregister.InfoRegisterInput;
import com.tinqinacademy.hotel.api.operations.system.inforregister.InfoRegisterOutput;
import com.tinqinacademy.hotel.api.operations.system.inforregister.InfoRegisterOutputList;
import com.tinqinacademy.hotel.core.services.operations.BaseOperationProcessor;
import com.tinqinacademy.hotel.core.utils.ErrorMapper;
import com.tinqinacademy.hotel.core.utils.SpecificationUtils;
import com.tinqinacademy.hotel.persistence.entities.Guest;
import com.tinqinacademy.hotel.persistence.repositories.GuestRepository;
import io.vavr.control.Either;
import io.vavr.control.Try;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.tinqinacademy.hotel.core.specifications.GuestSpecifications.*;
import static io.vavr.API.*;
import static io.vavr.Predicates.instanceOf;

@Slf4j
@Service
public class GetRegisterInfoOperationProcessor extends BaseOperationProcessor implements GetRegisterInfoOperation {
    private final GuestRepository guestRepository;

    @Autowired
    public GetRegisterInfoOperationProcessor(GuestRepository guestRepository,
                                             ConversionService conversionService,
                                             Validator validator,
                                             ErrorMapper errorMapper) {
        super(conversionService, validator, errorMapper);
        this.guestRepository = guestRepository;
    }

    @Override
    public Either<Errors, InfoRegisterOutputList> process(InfoRegisterInput input) {
        return validateInput(input)
                .flatMap(validated -> getRegisterInfo(input));
    }

    private Either<Errors, InfoRegisterOutputList> getRegisterInfo(InfoRegisterInput input){
        return Try.of(() -> {
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
                })
                .toEither()
                .mapLeft(throwable -> Match(throwable).of(
                        Case($(instanceOf(NotFoundException.class)), ex -> errorMapper.handleError(ex, HttpStatus.NOT_FOUND)),
                        Case($(), ex -> errorMapper.handleError(ex, HttpStatus.BAD_REQUEST))
                ));
    }
}
