package com.example.ytt.domain.inventory.controller;

import com.example.ytt.domain.inventory.dto.InboundLogDto;
import com.example.ytt.domain.inventory.dto.InboundReqDto;
import com.example.ytt.domain.inventory.service.InboundService;
import com.example.ytt.domain.medicine.dto.MedicineDto;
import com.example.ytt.domain.auth.security.CustomUserDetails;
import com.example.ytt.domain.user.dto.Role;
import com.example.ytt.global.common.annotation.SwaggerApi;
import com.example.ytt.global.common.response.ResponseDto;
import com.example.ytt.global.error.code.ExceptionType;
import com.example.ytt.global.util.ResponseUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/inbound")
@RequiredArgsConstructor
@Tag(name = "자판기 입고", description = "입고 API")
public class InboundController {

    private final InboundService inboundService;

    @PostMapping("/{machineId}")
    @SwaggerApi(summary = "입고 요청", description = "입고 요청 API", responseCode = "201")
    public ResponseEntity<ResponseDto<MedicineDto>> requestInbound(
            @PathVariable(value = "machineId") Long machineId,
            @RequestBody InboundReqDto reqDto,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        if (user.getRole().equals(Role.CUSTOMER)) {
            return ResponseUtil.error(ExceptionType.FORBIDDEN_USER, "입고 권한이 없습니다.", null);
        }

        return ResponseUtil.success(HttpStatus.CREATED, "입고 성공", inboundService.inboundMedicine(machineId, reqDto, user.getId()));
    }

    @GetMapping("/{machineId}")
    @SwaggerApi(summary = "입고 기록 조회", description = "입고 기록 조회 API, medicineId 또는 productCode로 특정 약 입고 기록 조회 가능, 없으면 전체 입고 기록 조회", responseCode = "200")
    public ResponseEntity<ResponseDto<List<InboundLogDto>>> getInboundHistory(
            @PathVariable(value = "machineId")                     Long machineId,
            @RequestParam(value = "medicineId", required = false)  Long medicineId,
            @RequestParam(value = "productCode", required = false) String productCode,
            @RequestParam(value = "startDate", required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")       LocalDateTime startDate,
            @RequestParam(value = "endDate", required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")       LocalDateTime endDate,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        if (user.getRole().equals(Role.CUSTOMER)) {
            return ResponseUtil.error(ExceptionType.FORBIDDEN_USER, "입고 기록을 조회할 권한이 없습니다.", null);
        }

        return ResponseUtil.success(inboundService.getInboundLogs(machineId, medicineId, productCode, startDate, endDate));
    }

}
