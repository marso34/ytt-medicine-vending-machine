package com.example.ytt.domain.vendingmachine.dto;

import com.example.ytt.domain.model.Address;
import com.example.ytt.domain.vendingmachine.domain.MachineState;
import com.example.ytt.domain.vendingmachine.domain.VendingMachine;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(title = "자판기 상세 Respone DTO", description = "자판기 상세 정보를 담은 DTO")
public record VendingMachineDetailDto(
        @Schema(description = "자판기 ID", example = "1")                      Long id,
        @Schema(description = "자판기 이름", example = "강릉원주대 자판기")    String name,
        @Schema(description = "자판기 상태", example = "OPERATING")            MachineState state,
        @Schema(description = "자판기 주소", example = "남원로 150")           String address,
        @Schema(description = "자판기 위도", example = "37.305121")            Double latitude,
        @Schema(description = "자판기 경도", example = "127.922653")           Double longitude,
        @Schema(description = "자판기 용량", example = "3")                    Integer capacity,
        @Schema(description = "즐겨찾기 여부", example = "true")               Boolean isFavorite
) {
    public static VendingMachineDetailDto of(Long id, String name, MachineState state, String address, Double latitude, Double longitude, Integer capacity, Boolean isFavorite) {
        return new VendingMachineDetailDto(id, name, state, address, latitude, longitude, capacity, isFavorite);
    }

    public static VendingMachineDetailDto of(Long id, String name, MachineState state, Address address, Integer capacity, Boolean isFavorite) {
        return new VendingMachineDetailDto(id, name, state, address.getAddressDetails(), address.getLocation().getY(), address.getLocation().getX(), capacity, isFavorite);
    }

    public static VendingMachineDetailDto of(VendingMachine vendingMachine, Boolean isFavorite) {
        return of(vendingMachine.getId(), vendingMachine.getName(), vendingMachine.getState(), vendingMachine.getAddress(), vendingMachine.getCapacity(), isFavorite);
    }
}
