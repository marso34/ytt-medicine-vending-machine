package com.example.ytt.domain.vendingmachine.dto;

import com.example.ytt.domain.medicine.dto.MedicineDto;
import com.example.ytt.domain.model.Address;
import com.example.ytt.domain.vendingmachine.domain.MachineState;
import com.example.ytt.domain.vendingmachine.domain.VendingMachine;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(title = "자판기 상세 Respone DTO", description = "자판기 상세 정보를 담은 DTO")
public record VendingMachineDetailDto(
        @Schema(description = "자판기 ID")     Long id,
        @Schema(description = "자판기 이름")    String name,
        @Schema(description = "자판기 상태")    MachineState state,
        @Schema(description = "자판기 주소")    String address,
        @Schema(description = "자판기 위도")    double latitude,
        @Schema(description = "자판기 경도")    double longitude,
        @Schema(description = "자판기 용량")    int capacity,
        @Schema(description = "즐겨찾기 여부")   boolean isFavorite,
        @Schema(description = "약 리스트")      List<MedicineDto> medicines
        ) {
    public static VendingMachineDetailDto of(Long id, String name, MachineState state, String address, double latitude, double longitude, int capacity, boolean isFavorite, List<MedicineDto> medicines) {
        return new VendingMachineDetailDto(id, name, state, address, latitude, longitude, capacity, isFavorite, medicines);
    }

    public static VendingMachineDetailDto of(Long id, String name, MachineState state, Address address, int capacity, boolean isFavorite, List<MedicineDto> medicines) {
        return new VendingMachineDetailDto(id, name, state, address.getAddressDetails(), address.getLocation().getY(), address.getLocation().getX(), capacity, isFavorite, medicines);
    }

    public static VendingMachineDetailDto of(VendingMachine vendingMachine, Boolean isFavorite, List<MedicineDto> medicines) {
        return of(vendingMachine.getId(), vendingMachine.getName(), vendingMachine.getState(), vendingMachine.getAddress(), vendingMachine.getCapacity(), isFavorite, medicines);
    }

    public static VendingMachineDetailDto of(VendingMachine vendingMachine, List<MedicineDto> medicines) {
        return of(vendingMachine, false, medicines);
    }

    public static VendingMachineDetailDto of(VendingMachine vendingMachine, Boolean isFavorite) {
        return of(vendingMachine, isFavorite, vendingMachine.getInventories().stream().map(MedicineDto::from).toList());
    }

    public static VendingMachineDetailDto from(VendingMachine vendingMachine) {
        return of(vendingMachine, false, vendingMachine.getInventories().stream().map(MedicineDto::from).toList());
    }
}
