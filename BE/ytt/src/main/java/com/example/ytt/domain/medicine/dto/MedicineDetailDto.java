package com.example.ytt.domain.medicine.dto;

import com.example.ytt.domain.inventory.domain.Inventory;
import com.example.ytt.domain.medicine.domain.Medicine;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(title = "약 상세 Respone DTO", description = "약 상세 정보를 담은 DTO")
public record MedicineDetailDto(
        @Schema(description = "약 ID")      Long id,
        @Schema(description = "제품이름")     String name,
        @Schema(description = "품목기준코드")  String productCode,
        @Schema(description = "제조업체")     String manufacturer,
        @Schema(description = "효능효과")     String efficacy,
        @Schema(description = "용법용량")     String usage,
        @Schema(description = "주의사항")     String precautions,
        @Schema(description = "유효기간")     String validityPeriod,
        @Schema(description = "제픔가격")     int price,
        @Schema(description = "재고")        int stock,
        @Schema(description = "이미지")       String imageURL,
        @Schema(description = "성분목록")     List<IngredientDto> ingredients
) {
    public static MedicineDetailDto of(Medicine medicine, List<IngredientDto> ingredients) {
        return new MedicineDetailDto(medicine.getId(), medicine.getName(), medicine.getProductCode(), medicine.getManufacturer(), medicine.getEfficacy(), medicine.getUsages(), medicine.getPrecautions(), medicine.getValidityPeriod(), medicine.getPrice(), 0, medicine.getImageURL(), ingredients);
    }

    public static MedicineDetailDto of(Medicine medicine, List<IngredientDto> ingredients, int stock) {
        return new MedicineDetailDto(medicine.getId(), medicine.getName(), medicine.getProductCode(), medicine.getManufacturer(), medicine.getEfficacy(), medicine.getUsages(), medicine.getPrecautions(), medicine.getValidityPeriod(), medicine.getPrice(), stock, medicine.getImageURL(), ingredients);
    }

    public static MedicineDetailDto from(Medicine medicine) {
        return of(medicine, medicine.getIngredients().stream().map(IngredientDto::from).toList(), 0);
    }

    public static MedicineDetailDto from(Inventory inventory) {
        Medicine medicine = inventory.getMedicine();

        return of(medicine, medicine.getIngredients().stream().map(IngredientDto::from).toList(), inventory.getQuantity());
    }
}
