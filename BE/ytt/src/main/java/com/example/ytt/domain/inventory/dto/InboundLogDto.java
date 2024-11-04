package com.example.ytt.domain.inventory.dto;

import com.example.ytt.domain.inventory.domain.InboundLog;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(title = "입고 로그 Response DTO", description = "입고 로그 정보를 담은 DTO")
public record InboundLogDto(
        @Schema(description = "입고 ID")      Long id,
        @Schema(description = "입고 자판기")    Long vendingMachineId,
        @Schema(description = "입고 약 ID")    Long medicineId,
        @Schema(description = "입고 약 이름")   String medicineName,
        @Schema(description = "입고량")        int quantity,
        @Schema(description = "입고 날짜")      String inboundedAt
) {
    public static InboundLogDto of(Long id, Long vendingMachineId, Long medicineId, String medicineName, int quantity, String inboundedAt) {
        return new InboundLogDto(id, vendingMachineId, medicineId, medicineName, quantity, inboundedAt);
    }

    public static InboundLogDto from(InboundLog inboundLog) {
        return of(inboundLog.getId(), inboundLog.getVendingMachine().getId(), inboundLog.getMedicine().getId(), inboundLog.getMedicine().getName(), inboundLog.getQuantity(), inboundLog.getInboundedAt().toString());
    }
}