package com.example.ytt.domain.vendingmachine.controller;

import com.example.ytt.domain.vendingmachine.dto.VendingMachineDetailDto;
import com.example.ytt.domain.vendingmachine.dto.VendingMachineDto;
import com.example.ytt.domain.vendingmachine.service.VendingMachineFindService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/vending-machine")
@RequiredArgsConstructor
@Tag(name = "자판기 조회", description = "자판기 조회 API")
public class VendingMachineController {

    private final VendingMachineFindService vendingMachineFindService;

    // TODO: path 경로 수정 필요 (현재 임의로 설정)

    @Operation(summary = "전체 자판기 조회", description = "모든 자판기 리스트 조회")
    @ApiResponse(responseCode = "200", description = "모든 자판기 조회 성공", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = VendingMachineDto.class))))
    @GetMapping("/all")
    public ResponseEntity<List<VendingMachineDto>> getAllVendingMachines() {
        return ResponseEntity.ok(vendingMachineFindService.getAllVendingMachines());
    }

    @Operation(summary = "이름으로 자판기 조회", description = "이름에 포함되어 있는 자판기 리스트 조회")
    @ApiResponse(responseCode = "200", description = "모든 자판기 조회 성공", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = VendingMachineDto.class))))
    @GetMapping("/name")
    public ResponseEntity<List<VendingMachineDto>> getVendingMachinesByName(@Parameter(description = "자판기 이름", example = "강릉원주대 자판기") @RequestParam("name") String name) {
        return ResponseEntity.ok(vendingMachineFindService.getVendingMachinesByName(name));
    }

    @Operation(summary = "주변 자판기 조회", description = "주어진 값으로부터 반경 2.5km 내 자판기 리스트 조회")
    @ApiResponse(responseCode = "200", description = "모든 자판기 조회 성공", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = VendingMachineDto.class))))
    @GetMapping("/nearby")
    public ResponseEntity<List<VendingMachineDto>> getVendingMachinesNearByLocation(
            @Parameter(description = "자판기의 위도", example = "37.305121")
            @RequestParam("latitude") Double latitude,
            @Parameter(description = "자판기의 경도", example = "127.922653")
            @RequestParam("longitude") Double longitude) {

        // TODO: 거리 파라미터 추가할지, 기본값 설정할지 고민 필요

        return ResponseEntity.ok(vendingMachineFindService.getVendingMachinesNearByLocation(latitude, longitude, 2500.0));
    }

    @Operation(summary = "자판기 ID로 조회", description = "자판기 ID로 자판기 조회")
    @ApiResponse(responseCode = "200", description = "자판기 조회 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = VendingMachineDetailDto.class)))
    @GetMapping("/{id}")
    public ResponseEntity<VendingMachineDetailDto> getVendingMachineById(@PathVariable(value = "id") Long id) {
        return ResponseEntity.ok(vendingMachineFindService.getVendingMachineDetail(id));
    }
}
