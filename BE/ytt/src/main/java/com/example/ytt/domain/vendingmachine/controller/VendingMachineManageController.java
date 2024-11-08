package com.example.ytt.domain.vendingmachine.controller;

import com.example.ytt.domain.inventory.dto.InboundReqDto;
import com.example.ytt.domain.user.auth.security.CustomUserDetails;
import com.example.ytt.domain.user.dto.Role;
import com.example.ytt.domain.vendingmachine.dto.VendingMachineDetailDto;
import com.example.ytt.domain.vendingmachine.dto.VendingMachineReqDto;
import com.example.ytt.domain.vendingmachine.service.VendingMachineManageService;
import com.example.ytt.global.common.annotation.SwaggerApi;
import com.example.ytt.global.common.response.ResponseDto;
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
@Tag(name = "자판기 기본", description = "자판기 생성, 수정, 삭제 API")
public class VendingMachineManageController {

    private final VendingMachineManageService vendingMachineService;

    @PostMapping("/create")
    @SwaggerApi(summary = "자판기 생성", description = "자판기 생성 API", implementation = ResponseDto.class)
    public ResponseEntity<ResponseDto<VendingMachineDetailDto>> createVendingMachine(@RequestBody VendingMachineReqDto reqDto, @AuthenticationPrincipal CustomUserDetails user) {
        if (user.getRole().equals(Role.CUSTOMER)) {
            log.warn("WARN: {}", "자판기를 생성할 권한이 없습니다. (권한: CUSTOMER)"); // TODO: 아래로 변경 필요
//            return ResponseUtil.error(ExceptionType.FORBIDDEN_USER, "자판기를 생성할 권한이 없습니다.", null);
        }

        return ResponseUtil.success(HttpStatus.CREATED, "자판기 생성 완료", vendingMachineService.createVendingMachine(reqDto));
    }

    // 자판기에 약 등록
    @PostMapping("/add-medicine")
    @SwaggerApi(summary = "자판기에 약 등록", description = "자판기에 약 등록 API", implementation = ResponseDto.class)
    public ResponseEntity<ResponseDto<VendingMachineDetailDto>> addMedicineToVendingMachine(@RequestBody InboundReqDto reqDto, @AuthenticationPrincipal CustomUserDetails user) {
        if (user.getRole().equals(Role.CUSTOMER)) {
            log.warn("WARN: {}", "자판기에 약을 등록할 권한이 없습니다. (권한: CUSTOMER)"); // TODO: 아래로 변경 필요
//            return ResponseUtil.error(ExceptionType.FORBIDDEN_USER, "자판기에 약을 등록할 권한이 없습니다.", null);
        }

        return ResponseUtil.success(HttpStatus.CREATED, "자판기 약 등록 완료" ,vendingMachineService.addMedicineToVendingMachine(reqDto));
    }

    // 자판기 운영 상태 변경

    // 자판기 약 삭제

    // 자판기 삭제
}
