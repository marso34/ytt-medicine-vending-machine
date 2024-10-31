package com.example.ytt.domain.medicine.controller;

import com.example.ytt.domain.medicine.dto.MedicineDetailDto;
import com.example.ytt.domain.medicine.dto.MedicineDto;
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

import java.util.List;

@RestController
@RequestMapping("/medicine")
@RequiredArgsConstructor
@Tag(name = "약품", description = "약품 API")
public class MedicineController {

    private final MedicineFindService medicineFindService;

    private final MedicineRegisterService medicineRegisterService;

    @PostMapping("/test")
    @SwaggerApi(summary = "약품 저장", description = "공공데이터에서 약품 정보를 찾아서 저장 ", implementation = ResponseDto.class)
    public ResponseEntity<ResponseDto<Boolean>> test2(@RequestBody MedicineRequestDto medicineRequestDto) {
        return ResponseUtil.success(medicineRegisterService.resiterMedicineByProductCode(medicineRequestDto));
    }

    @GetMapping("/all")
    @SwaggerApi(summary = "전체 약품 조회", description = "모든 약품 리스트 조회", implementation = ResponseDto.class)
    public ResponseEntity<ResponseDto<List<MedicineDto>>> getAllMedicines() {
        return ResponseUtil.success(medicineFindService.getAllMedicines());
    }

    @GetMapping("/name")
    @SwaggerApi(summary = "이름으로 약품 조회", description = "이름에 포함되어 있는 약품 리스트 조회", implementation = ResponseDto.class)
    public ResponseEntity<ResponseDto<List<MedicineDto>>> getMedicinesByName(@RequestParam("name") String name) {
        return ResponseUtil.success(medicineFindService.getMedicinesByName(name));
    }

    @GetMapping("/manufacturer")
    @SwaggerApi(summary = "제조사로 약품 조회", description = "제조사에 포함되어 있는 약품 리스트 조회", implementation = ResponseDto.class)
    public ResponseEntity<ResponseDto<List<MedicineDto>>> getMedicinesByManufacturer(@RequestParam("manufacturer") String manufacturer) {
        return ResponseUtil.success(medicineFindService.getMedicinesByManufacturer(manufacturer));
    }

    @GetMapping("/ingredient")
    @SwaggerApi(summary = "성분으로 약품 조회", description = "특정 성분이 포함된 약품 리스트 조회", implementation = ResponseDto.class)
    public ResponseEntity<ResponseDto<List<MedicineDto>>> findMedicineByIngredientId(@RequestParam("ingredientId") Long ingredientId) {
        return ResponseUtil.success(medicineFindService.findMedicineByIngredientId(ingredientId));
    }

    @GetMapping("/{id}")
    @SwaggerApi(summary = "약품 ID로 조회", description = "약품 ID로 약품 조회", implementation = ResponseDto.class)
    public ResponseEntity<ResponseDto<MedicineDetailDto>> getMedicineById(@PathVariable(value = "id") Long id) {
        return ResponseUtil.success(medicineFindService.getMedicineById(id));
    }

    @GetMapping("/productCode")
    @SwaggerApi(summary = "약품 코드로 조회", description = "약품 코드로 약품 조회", implementation = ResponseDto.class)
    public ResponseEntity<ResponseDto<MedicineDetailDto>> getMedicineByProductCode(@RequestParam("productCode") String productCode) {
        return ResponseUtil.success(medicineFindService.getMedicinesByProductCode(productCode));
    }

}
