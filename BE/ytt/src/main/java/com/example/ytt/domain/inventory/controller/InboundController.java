package com.example.ytt.domain.inventory.controller;

import com.example.ytt.domain.inventory.dto.InboundLogDto;
import com.example.ytt.domain.inventory.dto.InboundReqDto;
import com.example.ytt.domain.inventory.service.InboundService;
import com.example.ytt.domain.medicine.dto.MedicineDto;
import com.example.ytt.global.common.annotation.SwaggerApi;
import com.example.ytt.global.common.response.ResponseDto;
import com.example.ytt.global.util.ResponseUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/inbound")
@RequiredArgsConstructor
@Tag(name = "입고", description = "입고 API")
public class InboundController {

    private final InboundService inboundService;

    // 입고 요청
    @PostMapping("/request")
    @SwaggerApi(summary = "입고 요청", description = "입고 요청 API", implementation = InboundReqDto.class)
    public ResponseEntity<ResponseDto<MedicineDto>> requestInbound(@RequestBody InboundReqDto reqDto) {
        return ResponseUtil.success(inboundService.inboundMedicine(reqDto));
    }

    // 입고 기록 조회 (모든 약)
    @GetMapping("/history")
    @SwaggerApi(summary = "입고 기록 조회", description = "입고 기록 조회 API", implementation = ResponseDto.class)
    public ResponseEntity<ResponseDto<List<InboundLogDto>>> getInboundAllHistory(@RequestParam("machineId") Long machineId) {
        return ResponseUtil.success(inboundService.getInboundAllLogs(machineId));
    }

    // 입고 기록 조회 (특정 약)
    @GetMapping("/history/{id}")
    @SwaggerApi(summary = "입고 기록 조회", description = "입고 기록 조회 API", implementation = ResponseDto.class)
    public ResponseEntity<ResponseDto<List<InboundLogDto>>> getInboundHistory(@PathVariable("id") Long machineId, @RequestParam("medicineId") Long medicineId) {
        return ResponseUtil.success(inboundService.getInboundLogs(machineId, medicineId));
    }


}
