package com.example.ytt.domain.medicine.controller;

import com.example.ytt.domain.medicine.dto.MedicineRequestDto;
import com.example.ytt.domain.medicine.service.MedicineFindService;
import com.example.ytt.domain.medicine.service.MedicineRegisterService;
import com.example.ytt.global.common.annotation.SwaggerApi;
import com.example.ytt.global.common.response.ResponseDto;
import com.example.ytt.global.util.ResponseUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/medicine")
@RequiredArgsConstructor
@Tag(name = "약품 조회", description = "약품 조회 API")
public class MedicineController {

    private final MedicineFindService medicineService;

    private final MedicineRegisterService medicineRegisterService;

    @PostMapping("/test")
    @SwaggerApi(summary = "약품 저장", description = "공공데이터에서 약품 정보를 찾아서 저장 ", implementation = ResponseDto.class)
    public ResponseEntity<ResponseDto<Boolean>> test2(@RequestBody MedicineRequestDto medicineRequestDto) {
        return ResponseUtil.success(medicineRegisterService.resiterMedicineByProductCode(medicineRequestDto));
    }

}
