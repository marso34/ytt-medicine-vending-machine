package com.example.ytt.domain.medicine.dto;

import com.example.ytt.domain.medicine.domain.Ingredient;
import com.example.ytt.domain.medicine.domain.MedicineIngredient;
import com.example.ytt.domain.medicine.domain.Pharmacopeia;
import com.example.ytt.domain.medicine.domain.Unit;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(title = "성분 Respone DTO", description = "성분 정보를 담은 DTO")
public record IngredientDto(
        @Schema(description = "ID")     Long id,
        @Schema(description = "이름")    String name,
        @Schema(description = "효능")    String efficacy,
        @Schema(description = "분량")    double quantity,
        @Schema(description = "단위")    Unit unit,
        @Schema(description = "약전")    Pharmacopeia pharmacopeia
) {

    public static IngredientDto of(Long id, String name, String efficacy, double quantity, Unit unit, Pharmacopeia pharmacopeia) {
        return new IngredientDto(id, name, efficacy, quantity, unit, pharmacopeia);
    }

    public static IngredientDto from(MedicineIngredient medicineIngredient) {
        Ingredient ingredient = medicineIngredient.getIngredient();

        return of(ingredient.getId(), ingredient.getName(), ingredient.getEfficacy(), medicineIngredient.getQuantity(), medicineIngredient.getUnit(), ingredient.getPharmacopeia());
    }

}
