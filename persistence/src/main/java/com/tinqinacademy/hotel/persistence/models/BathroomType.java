package com.tinqinacademy.hotel.persistence.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum BathroomType {
    PRIVATE("private"),
    SHARED("shared"),
    UNKNOWN("");
    private final String code;

    BathroomType(String code){
        this.code = code;
    };

    @JsonCreator
    public static BathroomType getByCode(String code){
        return Arrays.stream(BathroomType.values())
                .filter(type -> type.toString().equals(code))
                .findFirst()
                .orElse(BathroomType.UNKNOWN);
    }

    @JsonValue
    public String toString(){
        return code;
    }
}
