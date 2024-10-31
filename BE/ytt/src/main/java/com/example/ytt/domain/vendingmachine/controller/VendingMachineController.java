package com.example.ytt.domain.vendingmachine.controller;

import com.example.ytt.domain.vendingmachine.dto.TempReqDto;
import com.example.ytt.domain.vendingmachine.dto.VendingMachineDetailDto;
import com.example.ytt.domain.vendingmachine.dto.VendingMachineReqDto;
import com.example.ytt.domain.vendingmachine.service.VendingMachineService;
import com.example.ytt.global.common.annotation.SwaggerApi;
import com.example.ytt.global.common.response.ResponseDto;
import com.example.ytt.global.util.ResponseUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/vending-machine")
@RequiredArgsConstructor
@Tag(name = "자판기 기본", description = "자판기 생성, 수정, 삭제 API")
public class VendingMachineController {

    private final VendingMachineService vendingMachineService;

    @PostMapping("/create")
    @SwaggerApi(summary = "자판기 생성", description = "자판기 생성 API", implementation = ResponseDto.class)
    public ResponseEntity<ResponseDto<VendingMachineDetailDto>> createVendingMachine(@RequestBody VendingMachineReqDto reqDto) {
        return ResponseUtil.success(vendingMachineService.createVendingMachine(reqDto));
    }

    // 자판기에 약 등록
    @PostMapping("/add-medicine")
    @SwaggerApi(summary = "자판기에 약 등록", description = "자판기에 약 등록 API", implementation = ResponseDto.class)
    public ResponseEntity<ResponseDto<VendingMachineDetailDto>> addMedicineToVendingMachine(@RequestBody TempReqDto reqDto) {
        return ResponseUtil.success(vendingMachineService.addMedicineToVendingMachine(reqDto));
    }
    
}
