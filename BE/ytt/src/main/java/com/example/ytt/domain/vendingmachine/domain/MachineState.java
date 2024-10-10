package com.example.ytt.domain.vendingmachine.domain;

import lombok.Getter;

@Getter
public enum MachineState {
    OPERATING, // 운영중
    OUT_OF_STOCK, // 품절
    ERROR, // 오류
    WARNING, // 경고
    MAINTENANCE, // 점검
    UNKNOWN // 알수없음
}
