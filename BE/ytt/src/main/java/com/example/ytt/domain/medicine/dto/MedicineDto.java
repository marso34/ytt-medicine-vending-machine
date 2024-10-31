package com.example.ytt.domain.medicine.dto;

import com.example.ytt.domain.inventory.domain.Inventory;
import com.example.ytt.domain.medicine.domain.Medicine;
import com.example.ytt.domain.medicine.domain.MedicineIngredient;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(title = "약 Respone DTO", description = "약 정보를 담은 DTO")
public record MedicineDto(
        @Schema(description = "ID")     Long id,
        @Schema(description = "이름")    String name,
        @Schema(description = "설명")    String description,
        @Schema(description = "가격")    int price,
        @Schema(description = "이미지")   String imageURL
) {
    public static MedicineDto of(Long id, String name, String description, int price, String imageURL) {
        return new MedicineDto(id, name, description, price, imageURL);
    }

    public static MedicineDto from(Inventory inventory) {
        return from(inventory.getMedicine());
    }

    public static MedicineDto from(MedicineIngredient medicineIngredient) {
        return from(medicineIngredient.getMedicine());
    }

    public static MedicineDto from(Medicine medicine) {
        // description - 약의 효능을 설명으로
        return new MedicineDto(medicine.getId(), medicine.getName(), medicine.getEfficacy(), medicine.getPrice(), medicine.getImageURL());
    }
}
