package com.example.ytt.domain.vendingmachine.controller;

import com.example.ytt.domain.inventory.dto.InboundReqDto;
import com.example.ytt.domain.user.auth.security.CustomUserDetails;
import com.example.ytt.domain.user.dto.Role;
import com.example.ytt.domain.vendingmachine.domain.MachineState;
import com.example.ytt.domain.vendingmachine.dto.VendingMachineDetailDto;
import com.example.ytt.domain.vendingmachine.dto.VendingMachineReqDto;
import com.example.ytt.domain.vendingmachine.service.FavoriteService;
import com.example.ytt.domain.vendingmachine.service.VendingMachineManageService;
import com.example.ytt.global.common.annotation.SwaggerApi;
import com.example.ytt.global.common.response.ResponseDto;
import com.example.ytt.global.error.code.ExceptionType;
import com.example.ytt.global.util.ResponseUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/vending-machine")
@RequiredArgsConstructor
@Tag(name = "자판기 관리", description = "자판기 생성, 수정, 삭제 API")
public class VendingMachineManageController {

    private final VendingMachineManageService vendingMachineService;
    private final FavoriteService favoriteService;

    @PostMapping
    @SwaggerApi(summary = "자판기 생성", description = "자판기 생성 API", responseCode = "201")
    public ResponseEntity<ResponseDto<VendingMachineDetailDto>> createVendingMachine(@RequestBody VendingMachineReqDto reqDto, @AuthenticationPrincipal CustomUserDetails user) {
        if (user.getRole().equals(Role.CUSTOMER)) {
            return ResponseUtil.error(ExceptionType.FORBIDDEN_USER, "자판기를 생성할 권한이 없습니다.", null);
        }

        return ResponseUtil.success(HttpStatus.CREATED, "자판기 생성 완료", vendingMachineService.createVendingMachine(reqDto, user.getId()));
    }

    @PatchMapping("/{machineId}")
    @SwaggerApi(summary = "자판기 운영 상태 변경", description = "자판기 운영 상태 변경 API")
    public ResponseEntity<ResponseDto<VendingMachineDetailDto>> openVendingMachine(
            @PathVariable(value = "machineId") Long machineId,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        if (user.getRole().equals(Role.CUSTOMER)) {
            return ResponseUtil.error(ExceptionType.FORBIDDEN_USER, "자판기 운영 상태를 변경할 권한이 없습니다.", null);
        }

        return ResponseUtil.success(vendingMachineService.openVendingMachine(machineId, MachineState.OPERATING));
    }

    // TODO: 자판기 삭제
//    @DeleteMapping("/{machineId}")

    @PostMapping("/{machineId}/medicine")
    @SwaggerApi(summary = "자판기에 약 등록", description = "자판기에 약 등록 API")
    public ResponseEntity<ResponseDto<VendingMachineDetailDto>> addMedicineToVendingMachine(
            @PathVariable(value = "machineId") Long machineId,
            @RequestBody InboundReqDto reqDto,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        if (user.getRole().equals(Role.CUSTOMER)) {
            return ResponseUtil.error(ExceptionType.FORBIDDEN_USER, "자판기에 약을 등록할 권한이 없습니다.", null);
        }

        return ResponseUtil.success(HttpStatus.CREATED, "자판기 약 등록 완료" ,vendingMachineService.addMedicineToVendingMachine(machineId, reqDto, user.getId()));
    }

    // TODO: 자판기 약 수정
//    @PatchMapping("/{machineId}/medicine/{medicineId}")

    // TODO: 자판기 약 삭제
//    @DeleteMapping("/{machineId}/medicine/{medicineId}")

    @PostMapping("/favorites/{machineId}")
    @SwaggerApi(summary = "즐겨찾기 추가", description = "자판기를 즐겨찾기에 추가하는 API")
    public ResponseEntity<ResponseDto<Boolean>> addFavorite(@PathVariable(value = "machineId") Long productId, @AuthenticationPrincipal CustomUserDetails user) {
        favoriteService.addFavorite(user.getId(), productId);
        return ResponseUtil.success(null);
    }

    @DeleteMapping("/favorites/{machineId}")
    @SwaggerApi(summary = "즐겨찾기 삭제", description = "자판기를 즐겨찾기에서 삭제하는 API")
    public ResponseEntity<ResponseDto<Boolean>> removeFavorite(@PathVariable(value = "machineId") Long productId, @AuthenticationPrincipal CustomUserDetails user) {
        boolean isRemoved = favoriteService.removeFavorite(user.getId(), productId);
        return ResponseUtil.success(isRemoved);
    }

}
