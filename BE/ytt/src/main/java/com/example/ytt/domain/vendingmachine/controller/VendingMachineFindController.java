package com.example.ytt.domain.vendingmachine.controller;

import com.example.ytt.domain.inventory.service.InventoryService;
import com.example.ytt.domain.medicine.dto.MedicineDetailDto;
import com.example.ytt.domain.user.auth.security.CustomUserDetails;
import com.example.ytt.domain.vendingmachine.dto.VendingMachineDetailDto;
import com.example.ytt.domain.vendingmachine.dto.VendingMachineDto;
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
    private final InventoryService inventoryService;

    // TODO: path 경로 수정 필요 (현재 임의로 설정)

    // 자판기 리스트 조회

    @GetMapping("/all")
    @SwaggerApi(summary = "전체 자판기 조회", description = "모든 자판기 리스트 조회", implementation = ResponseDto.class)
    public ResponseEntity<ResponseDto<List<VendingMachineDto>>> getAllVendingMachines() {
        return ResponseUtil.success(vendingMachineFindService.getAllVendingMachines());
    }

    @GetMapping("/name")
    @SwaggerApi(summary = "이름으로 자판기 조회", description = "이름에 포함되어 있는 자판기 리스트 조회", implementation = ResponseDto.class)
    public ResponseEntity<ResponseDto<List<VendingMachineDto>>> getVendingMachinesByName(@Parameter(description = "자판기 이름", example = "강릉원주대 자판기") @RequestParam("name") String name) {
        return ResponseUtil.success(vendingMachineFindService.getVendingMachinesByName(name));
    }

    @GetMapping("/medicine")
    @SwaggerApi(summary = "약품으로 자판기 조회", description = "약품이 포함되어 있는 자판기 리스트 조회", implementation = ResponseDto.class)
    public ResponseEntity<ResponseDto<List<VendingMachineDto>>> getVendingMachinesByMedicine(@RequestParam("medicine_id") Long medicine_id) {
        return ResponseUtil.success(vendingMachineFindService.getVendingMachinesByMedicine(medicine_id));
    }

    @GetMapping("/nearby")
    @SwaggerApi(summary = "주변 자판기 조회", description = "주어진 값으로부터 반경 2.5km 내 자판기 리스트 조회", implementation = ResponseDto.class)
    public ResponseEntity<ResponseDto<List<VendingMachineDto>>> getVendingMachinesNearByLocation(
            @Parameter(description = "자판기의 위도", example = "37.305121")
            @RequestParam("latitude") Double latitude,
            @Parameter(description = "자판기의 경도", example = "127.922653")
            @RequestParam("longitude") Double longitude) {

        // TODO: 거리 파라미터 추가할지, 기본값 설정할지 고민 필요
        // TODO: 위도, 경도 범위 제한 추가할지 고민 필요

        return ResponseUtil.success(vendingMachineFindService.getVendingMachinesNearByLocation(latitude, longitude, 2500.0));
    }

    // 자판기 상세 조회

    @GetMapping("/{id}")
    @SwaggerApi(summary = "자판기 ID로 조회", description = "자판기 ID로 자판기 조회", implementation = ResponseDto.class)
    public  ResponseEntity<ResponseDto<VendingMachineDetailDto>> getVendingMachineById(@PathVariable(value = "id") Long machineId, @AuthenticationPrincipal CustomUserDetails user) {
        log.info("user: {}", user.getUsername());

        return ResponseUtil.success(vendingMachineFindService.getVendingMachineDetail(machineId, 1L)); // user.getId()로 수정 필요
    }

    @GetMapping("/{id}/inventory")
    @SwaggerApi(summary = "자판기 재고 조회", description = "자판기 ID로 자판기의 재고 조회", implementation = ResponseDto.class)
    public ResponseEntity<ResponseDto<List<MedicineDetailDto>>> getVendingMachineInventory(@PathVariable(value = "id") Long id) {
        return ResponseUtil.success(inventoryService.getMedicinesByVendingMachine(id));
    }

    @GetMapping("/{id}/medicine")
    @SwaggerApi(summary = "자판기의 특정 약 조회", description = "자판기의 특정 약 상세 조회", implementation = ResponseDto.class)
    public ResponseEntity<ResponseDto<MedicineDetailDto>> getVendingMachineById(@PathVariable(value = "id") Long machineId, @RequestParam("medicineId") Long medicineId) {
        return ResponseUtil.success(inventoryService.getMedicineByInventory(machineId, medicineId));
    }

}
