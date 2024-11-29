package com.example.ytt.domain.order.util;

import java.util.UUID;

/**
 * UUID 유틸리티 클래스
 */
public class UUIDutil {

    // private 생성자: 인스턴스화를 방지
    private UUIDutil() {
        throw new UnsupportedOperationException("Utility Class");
    }

    /**
     * UUID 생성
     * @return UUID
     */
    public static UUID createUUID() {
        return java.util.UUID.randomUUID();
    }

    /**
     * uuid 생성
     * @return uuid
     */
    public static UUID convertUUID(String uuid) {
        try {
            return java.util.UUID.fromString(uuid);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("uuid 형식이 아닙니다: " + uuid);
        }
    }

    /**
     * BINARY(16) UUID를 HEX String 변환
     * @return String
     */
    public static String toHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }

}
