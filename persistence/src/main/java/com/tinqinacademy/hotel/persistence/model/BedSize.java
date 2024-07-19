package com.tinqinacademy.hotel.persistence.model;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum BedSize {
    SINGLE("single", 1),
    DOUBLE("double", 2),
    SMALLDOUBLE("smalldouble", 2),
    KINGSIZE("kingsize", 3),
    QUEENSIZE("queensize", 3),
    UNKNOWN("",0);
    private final String code;
    private final Integer count;

    BedSize(String code, Integer count){
        this.code = code;
        this.count = count;
    }

    @JsonCreator
    public static BedSize getByCode(String code){
        return Arrays.stream(BedSize.values())
                .filter(type -> type.toString().equals(code))
                .findFirst()
                .orElse(BedSize.UNKNOWN);
    }

    @JsonValue
    public String toString(){
        return code;
    }
}
