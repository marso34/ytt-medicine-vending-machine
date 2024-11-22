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
@Tag(name = "약품 조회", description = "약품 API")
public class MedicineFindController {

    private final MedicineFindService medicineFindService;

    @GetMapping("/all")
    @SwaggerApi(summary = "전체 약품 조회", description = "모든 약품 리스트 조회")
    public ResponseEntity<ResponseDto<List<MedicineDto>>> getAllMedicines() {
        return ResponseUtil.success(medicineFindService.getAllMedicines());
    }

    @GetMapping("/name")
    @SwaggerApi(summary = "이름으로 약품 조회 (deprecated)", description = "이름에 포함되어 있는 약품 리스트 조회")
    public ResponseEntity<ResponseDto<List<MedicineDto>>> getMedicinesByName(@RequestParam("name") String name) {
        return ResponseUtil.success(medicineFindService.getMedicinesByName(name));
    }

    @GetMapping("/manufacturer")
    @SwaggerApi(summary = "제조사로 약품 조회 (deprecated)", description = "제조사에 포함되어 있는 약품 리스트 조회")
    public ResponseEntity<ResponseDto<List<MedicineDto>>> getMedicinesByManufacturer(@RequestParam("manufacturer") String manufacturer) {
        return ResponseUtil.success(medicineFindService.getMedicinesByManufacturer(manufacturer));
    }

    @GetMapping("/ingredient")
    @SwaggerApi(summary = "성분으로 약품 조회 (deprecated)", description = "특정 성분이 포함된 약품 리스트 조회")
    public ResponseEntity<ResponseDto<List<MedicineDto>>> findMedicineByIngredientId(@RequestParam("ingredientId") Long ingredientId) {
        return ResponseUtil.success(medicineFindService.findMedicineByIngredientId(ingredientId));
    }

    @GetMapping("/{id}")
    @SwaggerApi(summary = "약품 ID로 조회 (deprecated)", description = "약품 ID로 약품 조회")
    public ResponseEntity<ResponseDto<MedicineDetailDto>> getMedicineById(@PathVariable(value = "id") Long id) {
        return ResponseUtil.success(medicineFindService.getMedicineById(id));
    }

    @GetMapping("/productCode")
    @SwaggerApi(summary = "약품 코드로 조회 (deprecated)", description = "약품 코드로 약품 조회")
    public ResponseEntity<ResponseDto<MedicineDetailDto>> getMedicineByProductCode(@RequestParam("productCode") String productCode) {
        return ResponseUtil.success(medicineFindService.getMedicinesByProductCode(productCode));
    }

    /* -- QueryDSL을 사용한 코드 -- */

    @GetMapping
    @SwaggerApi(summary = "약품 기본 조회", description = "약품 리스트 조회")
    public ResponseEntity<ResponseDto<List<MedicineDto>>> getMedicines(
            @RequestParam(value = "name", required = false)           String name,
            @RequestParam(value = "manufacturer", required = false)   String manufacturer,
            @RequestParam(value = "ingredientName", required = false) String ingredientName
    ) {
        return ResponseUtil.success(medicineFindService.getMedicines(name, manufacturer, ingredientName));
    }

    @GetMapping("/detail")
    @SwaggerApi(summary = "약품 상세 조회", description = "약품 상세 정보 조회 (id or productCode)")
    public ResponseEntity<ResponseDto<MedicineDetailDto>> getMedicineDetail(
            @RequestParam(value = "medicineId", required = false)  Long medicineId,
            @RequestParam(value = "productCode", required = false) String productCode
    ) {
        return ResponseUtil.success(medicineFindService.getMedicineDetail(medicineId, productCode));
    }
}
