package com.example.ytt.domain.medicine.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MedicineTest {

    private Medicine medicine;

    @BeforeEach
    void setUp() {
        medicine = Medicine.builder()
                .name("name")
                .productCode("productCode")
                .manufacturer("manufacturer")
                .efficacy("efficacy")
                .usageInstructions("usageInstructions")
                .precautions("precautions")
                .sideEffects("sideEffects")
                .price(1000)
                .build();
    }

    @DisplayName("약 생성 테스트")
    @Test
    void createMedicine() {
        assertThat(medicine).isNotNull();
        assertThat(medicine.getName()).isEqualTo("name");
        assertThat(medicine.getProductCode()).isEqualTo("productCode");
        assertThat(medicine.getManufacturer()).isEqualTo("manufacturer");
        assertThat(medicine.getEfficacy()).isEqualTo("efficacy");
        assertThat(medicine.getUsageInstructions()).isEqualTo("usageInstructions");
        assertThat(medicine.getPrecautions()).isEqualTo("precautions");
        assertThat(medicine.getSideEffects()).isEqualTo("sideEffects");
        assertThat(medicine.getPrice()).isEqualTo(1000);
    }

    @DisplayName("성분 추가")
    @Test
    void addIngredient() {
        final Ingredient ingredient = Ingredient.of("ingredient", "efficacy");

        medicine.addIngredient(ingredient);
        medicine.addIngredient(ingredient);
        medicine.addIngredient(ingredient);

        assertThat(medicine.getIngredients())
                .hasSize(3)
                .extracting(v -> v.getIngredient().equals(ingredient))
                .isNotNull();
    }

    @DisplayName("성분 삭제")
    @Test
    void removeIngredient() {
        final Ingredient ingredient = Ingredient.of("ingredient", "efficacy");

        medicine.addIngredient(ingredient);
        medicine.addIngredient(Ingredient.of("ingredient2", "efficacy"));
        medicine.addIngredient(Ingredient.of("ingredient3", "efficacy"));

        assertThat(medicine.getIngredients()).hasSize(3);
        assertThat(medicine.getIngredients())
                .filteredOn(m -> m.getIngredient().equals(ingredient))
                .isNotNull()
                .hasSize(1);

        medicine.removeIngredient(ingredient);

        assertThat(medicine.getIngredients()).hasSize(2);
        assertThat(medicine.getIngredients())
                .filteredOn(m -> m.getIngredient().equals(ingredient))
                .isEmpty();
    }

}