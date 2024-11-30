package com.example.ytt.domain.order.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OrderState {
    PENDING("pending", "잠시만 기다려주세요."),
    STORED("stored", "자판기에서 약을 가져가세요"),
    COMPLETED("completed", "주문이 완료되었습니다"),
    CANCELED("canceled","주문이 취소됐어요. 다음에 다시 주문해주세요"),
    UNKNOWN("unknown", "알 수 없는 오류가 발생했습니다.");
    
    private final String state;
    private final String message;

    public static OrderState from(String state) {
        for (OrderState orderState : OrderState.values()) {
            if (orderState.state.equalsIgnoreCase(state)) {
                return orderState;
            }
        }
        return UNKNOWN;
    }
}
