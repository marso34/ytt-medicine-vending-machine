package com.example.ytt.domain.medicine.controller;

import com.example.ytt.domain.medicine.dto.MedicineRequestDto;
import com.example.ytt.domain.medicine.service.MedicineRegisterService;
import com.example.ytt.global.common.annotation.SwaggerApi;
import com.example.ytt.global.common.response.ResponseDto;
import com.example.ytt.global.util.ResponseUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/medicine")
@RequiredArgsConstructor
@Tag(name = "약품 관리", description = "약품 API")
public class MedicineManageController {

    private final MedicineRegisterService medicineRegisterService;

    // 약품 저장

    @PostMapping
    @SwaggerApi(summary = "약품 생성", description = "이름으로 약품 정보를 찾아서 저장 (공공데이터)", responseCode = "201")
    public ResponseEntity<ResponseDto<Boolean>> registerMedicine(
            @RequestBody MedicineRequestDto medicineRequestDto) {
        return ResponseUtil.success(HttpStatus.CREATED, "약 생성 완료",medicineRegisterService.resisterMedicineByProductCode(medicineRequestDto));
    }

//    @PostMapping("/test2")
//    @SwaggerApi(summary = "약품 저장", description = "바코드로 약품 정보를 찾아서 저장 (공공데이터)", implementation = ResponseDto.class)
//    public ResponseEntity<ResponseDto<Boolean>> registerMedicine2(
//            @RequestBody MedicineRequestDto medicineRequestDto) {
//        return ResponseUtil.success(medicineRegisterService.resisterMedicineByBarcode("8806443000000", 1000));
//    }

}
