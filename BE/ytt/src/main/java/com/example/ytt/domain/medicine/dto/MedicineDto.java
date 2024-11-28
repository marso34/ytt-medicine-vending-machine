package com.example.ytt.domain.medicine.dto;

import com.example.ytt.domain.inventory.domain.Inventory;
import com.example.ytt.domain.medicine.domain.Medicine;
import com.example.ytt.domain.medicine.domain.MedicineIngredient;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(title = "약 Respone DTO", description = "약 정보를 담은 DTO")
public record MedicineDto(
        @Schema(description = "약 ID")      Long id,
        @Schema(description = "약 이름")     String name,
        @Schema(description = "품목기준코드")  String productCode,
        @Schema(description = "약 설명")     String description,
        @Schema(description = "약 가격")     int price,
        @Schema(description = "약 재고")     int stock,
        @Schema(description = "이미지")      String imageURL
) {
    public static MedicineDto of(Long id, String name, String productCode, String description, int price, int stock, String imageURL) {
        return new MedicineDto(id, name, productCode, description, price, stock, imageURL);
    }

    public static MedicineDto of(Medicine medicine, int stock) {
        // description - 약의 효능을 설명으로
        return new MedicineDto(medicine.getId(), medicine.getName(), medicine.getProductCode(), medicine.getEfficacy(), medicine.getPrice(), stock, medicine.getImageURL());
    }

    public static MedicineDto from(Medicine medicine) {
        // description - 약의 효능을 설명으로
        return of(medicine, 0);
    }

    public static MedicineDto from(Inventory inventory) {
        return of(inventory.getMedicine(), inventory.getQuantity());
    }

    public static MedicineDto from(MedicineIngredient medicineIngredient) {
        return from(medicineIngredient.getMedicine());
    }
}
