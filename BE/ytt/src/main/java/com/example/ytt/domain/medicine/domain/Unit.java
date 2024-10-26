package com.example.ytt.domain.medicine.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Unit {
    MG("밀리그램"),
    G("그램"),
    ML("밀리리터"),
    L("리터"),
    EA("개");

    private final String info;

    public static Unit from(String unit) {
        for (Unit u : Unit.values()) {
            if (u.info.equals(unit)) {
                return u;
            }
        }

        return G; // Default unit is G, 나중에 예외처리
    }
}
