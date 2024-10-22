package com.example.ytt.domain.medicine.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Pharmacopeia {
    KP("KP"),
    KPC("KPC"),
    KHP("KHP"),
    USP("USP"),
    JP("JP"),
    EP("EP"),
    BP("BP"),
    DAB("DAB"),
    PF("PF");

    private final String info;

    public static Pharmacopeia from(String info) {
        for (Pharmacopeia pharmacopeia : Pharmacopeia.values()) {
            if (pharmacopeia.info.equals(info)) {
                return pharmacopeia;
            }
        }

        return KP; // Default unit is KP, 나중에 예외처리
    }

}


