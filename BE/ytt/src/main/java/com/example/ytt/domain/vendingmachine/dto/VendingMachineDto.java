package com.example.ytt.domain.vendingmachine.dto;

import com.example.ytt.domain.model.Address;
import com.example.ytt.domain.vendingmachine.domain.MachineState;
import com.example.ytt.domain.vendingmachine.domain.VendingMachine;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(title = "자판기 Respone DTO", description = "자판기 정보를 담은 DTO")
public record VendingMachineDto(
        @Schema(description = "자판기 ID", example = "1")                     Long id,
        @Schema(description = "자판기 이름", example = "강릉원주대 자판기")   String name,
        @Schema(description = "자판기 상태", example = "OPERATING")           MachineState state,
        @Schema(description = "자판기 주소", example = "남원로 150")          String address,
        @Schema(description = "자판기 위도", example = "37.305121")           Double latitude,
        @Schema(description = "자판기 경도", example = "127.922653")          Double longitude
) {
    public static VendingMachineDto from(Long id, String name, MachineState state, String address, Double latitude, Double longitude) {
        return new VendingMachineDto(id, name, state, address, latitude, longitude);
    }

    // Point(address.getLocation)는 경도, 위도 순이라 getY(), getX() 순서로 작성
    public static VendingMachineDto from(Long id, String name, MachineState state, Address address) {
        return new VendingMachineDto(id, name, state, address.getAddressDetails(), address.getLocation().getY(), address.getLocation().getX());
    }

    public static VendingMachineDto from(VendingMachine vendingMachine) {
        return from(vendingMachine.getId(), vendingMachine.getName(), vendingMachine.getState(), vendingMachine.getAddress());
    }

}