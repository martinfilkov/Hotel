package com.tinqinacademy.hotel.api.model;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

public enum BedSize {
    SINGLE("single"),
    DOUBLE("double"),
    SMALLDOUBLE("smalldouble"),
    KINGSIZE("kingsize"),
    QUEENSIZE("queensize"),
    UNKNOWN("");
    private final String code;

    BedSize(String code){
        this.code = code;
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
