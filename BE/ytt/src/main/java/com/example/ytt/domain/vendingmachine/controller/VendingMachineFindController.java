package com.example.ytt.domain.vendingmachine.controller;

import com.example.ytt.domain.inventory.service.InventoryService;
import com.example.ytt.domain.medicine.dto.MedicineDetailDto;
import com.example.ytt.domain.medicine.dto.MedicineDto;
import com.example.ytt.domain.user.auth.security.CustomUserDetails;
import com.example.ytt.domain.vendingmachine.dto.VendingMachineDetailDto;
import com.example.ytt.domain.vendingmachine.dto.VendingMachineDto;
import com.example.ytt.domain.favorite.service.FavoriteService;
import com.example.ytt.domain.vendingmachine.service.VendingMachineFindService;
import com.example.ytt.global.common.annotation.SwaggerApi;
import com.example.ytt.global.common.response.ResponseDto;
import com.example.ytt.global.util.ResponseUtil;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/vending-machine")
@RequiredArgsConstructor
@Tag(name = "자판기 조회", description = "자판기 조회 API")
public class VendingMachineFindController {

    private final VendingMachineFindService vendingMachineFindService;
    private final FavoriteService favoriteService;
    private final InventoryService inventoryService;

    // 자판기 리스트 조회

    @GetMapping("/all")
    @SwaggerApi(summary = "전체 자판기 조회", description = "모든 자판기 리스트 조회", implementation = ResponseDto.class)
    public ResponseEntity<ResponseDto<List<VendingMachineDto>>> getAllVendingMachines() {
        return ResponseUtil.success(vendingMachineFindService.getAllVendingMachines());
    }

    @GetMapping("/name")
    @SwaggerApi(summary = "이름으로 자판기 조회 (deprecated)", description = "이름에 포함되어 있는 자판기 리스트 조회", implementation = ResponseDto.class)
    public ResponseEntity<ResponseDto<List<VendingMachineDto>>> getVendingMachinesByName(@Parameter(description = "자판기 이름", example = "강릉원주대 자판기") @RequestParam("name") String name) {
        return ResponseUtil.success(vendingMachineFindService.getVendingMachinesByName(name));
    }

    @GetMapping("/medicine")
    @SwaggerApi(summary = "약품으로 자판기 조회 (deprecated)", description = "약품이 포함되어 있는 자판기 리스트 조회", implementation = ResponseDto.class)
    public ResponseEntity<ResponseDto<List<VendingMachineDto>>> getVendingMachinesByMedicine(@RequestParam("medicine_id") Long medicineId) {
        return ResponseUtil.success(vendingMachineFindService.getVendingMachinesByMedicine(medicineId));
    }

    @GetMapping("/nearby")
    @SwaggerApi(summary = "주변 자판기 조회 (deprecated)", description = "주어진 값으로부터 반경 2.5km 내 자판기 리스트 조회", implementation = ResponseDto.class)
    public ResponseEntity<ResponseDto<List<VendingMachineDto>>> getVendingMachinesNearByLocation(
            @Parameter(description = "자판기의 위도", example = "37.305121")
            @RequestParam("latitude") Double latitude,
            @Parameter(description = "자판기의 경도", example = "127.922653")
            @RequestParam("longitude") Double longitude) {
        return ResponseUtil.success(vendingMachineFindService.getVendingMachinesNearByLocation(latitude, longitude, 2500.0));
    }

    /* -- QueryDSL을 사용한 코드 -- */

    @GetMapping
    @SwaggerApi(summary = "자판기 기본 조회", description = "일정 범위 내에 자판기 리스트 조회")
    public ResponseEntity<ResponseDto<List<VendingMachineDto>>> getVendingMachines(
            @RequestParam(value = "latitude", required = true)                           Double latitude,
            @RequestParam(value = "longitude", required = true)                          Double longitude,
            @RequestParam(value = "distance", required = false, defaultValue = "2500.0") Double distance,
            @RequestParam(value = "name", required = false)                              String name
    ) {
        return ResponseUtil.success(vendingMachineFindService.getVendingMachines(latitude, longitude, distance, name));
    }

    @GetMapping("/getByMedicine")
    @SwaggerApi(summary = "약품으로 자판기 조회", description = "약품이 포함되어 있는 자판기 리스트 조회")
    public ResponseEntity<ResponseDto<List<VendingMachineDto>>> getVendingMachinesByMedicine(
            @RequestParam(value = "latitude", required = false)                          Double latitude,
            @RequestParam(value = "longitude", required = false)                         Double longitude,
            @RequestParam(value = "distance", required = false, defaultValue = "2500.0") Double distance,
            @RequestParam(value = "medicine_id", required = true)                        Long medicineId
    ) {
        return ResponseUtil.success(vendingMachineFindService.getVendingMachinesByMedicine(latitude, longitude, distance, medicineId));
    }

    /* -- 자판기 상세 조회 -- */

    @GetMapping("/{machineId}")
    @SwaggerApi(summary = "자판기 ID로 조회", description = "자판기 ID로 자판기 조회")
    public  ResponseEntity<ResponseDto<VendingMachineDetailDto>> getVendingMachineById(@PathVariable(value = "machineId") Long machineId, @AuthenticationPrincipal CustomUserDetails user) {
        return ResponseUtil.success(vendingMachineFindService.getVendingMachineDetail(machineId, user.getId()));
    }

    @GetMapping("/{machineId}/medicines")
    @SwaggerApi(summary = "특정 자판기의 전체 재고 조회", description = "자판기 ID로 자판기의 재고 조회")
    public ResponseEntity<ResponseDto<List<MedicineDto>>> getMedicinesInVendingMachine(@PathVariable(value = "machineId") Long id) {
        return ResponseUtil.success(inventoryService.getMedicinesByVendingMachine(id));
    }

    @GetMapping("/{machineId}/medicine")
    @SwaggerApi(summary = "특정 자판기의 특정 약 조회", description = "자판기의 특정 약 상세 조회")
    public ResponseEntity<ResponseDto<MedicineDetailDto>> getMedicineInVendingMachine(@PathVariable(value = "machineId") Long machineId, @RequestParam("id") Long medicineId) {
        return ResponseUtil.success(inventoryService.getMedicineByInventory(machineId, medicineId));
    }

}
