package com.example.ytt.domain.medicine.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

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
                .usage("usage")
                .precautions("precautions")
                .validityPeriod("validityPeriod")
                .imageURL("imageURL")
                .price(1000)
                .build();

        medicine.addIngredient(Ingredient.of("ingredient1", Pharmacopeia.KP), 1, Unit.MG);
        medicine.addIngredient(Ingredient.of("ingredient2", Pharmacopeia.KHP), 1, Unit.G);
        medicine.addIngredient(Ingredient.of("ingredient3", Pharmacopeia.KHP), 1, Unit.G);
    }

    @DisplayName("약 생성 테스트")
    @Test
    void createMedicine() {
        assertThat(medicine).isNotNull();
        assertThat(medicine.getName()).isEqualTo("name");
        assertThat(medicine.getProductCode()).isEqualTo("productCode");
        assertThat(medicine.getManufacturer()).isEqualTo("manufacturer");
        assertThat(medicine.getEfficacy()).isEqualTo("efficacy");
        assertThat(medicine.getUsage()).isEqualTo("usage");
        assertThat(medicine.getPrecautions()).isEqualTo("precautions");
        assertThat(medicine.getValidityPeriod()).isEqualTo("validityPeriod");
        assertThat(medicine.getImageURL()).isEqualTo("imageURL");
        assertThat(medicine.getPrice()).isEqualTo(1000);

        final List<MedicineIngredient> ingredients = medicine.getIngredients();

        assertThat(ingredients).hasSize(3);
        assertThat(ingredients)
                .extracting(MedicineIngredient::getIngredient)
                .extracting(Ingredient::getName)
                .containsExactly("ingredient1", "ingredient2", "ingredient3");
    }

    @DisplayName("성분 추가")
    @Test
    void addIngredient() {
        final Ingredient ingredient = Ingredient.of("ingredient4", "efficacy", Pharmacopeia.KHP);

        medicine.addIngredient(ingredient, 1, "mg");
        medicine.addIngredient(ingredient, 2, "mg");
        medicine.addIngredient(ingredient, 3, "mg");

        assertThat(medicine.getIngredients())
                .hasSize(6)
                .extracting(v -> v.getIngredient().equals(ingredient))
                .isNotNull();
    }

    @DisplayName("성분 삭제")
    @Test
    void removeIngredient() {
        final Ingredient ingredient = Ingredient.of("ingredient4", "efficacy", "KP");

        medicine.addIngredient(ingredient, 1, "mg");
        medicine.addIngredient(Ingredient.of("ingredient5", "efficacy", "KP"), 2, "mg");
        medicine.addIngredient(Ingredient.of("ingredient6", "efficacy", "KP"), 3, "mg");

        assertThat(medicine.getIngredients()).hasSize(6);
        assertThat(medicine.getIngredients())
                .filteredOn(m -> m.getIngredient().equals(ingredient))
                .isNotNull()
                .hasSize(1);

        medicine.removeIngredient(ingredient);

        assertThat(medicine.getIngredients()).hasSize(5);
        assertThat(medicine.getIngredients())
                .filteredOn(m -> m.getIngredient().equals(ingredient))
                .isEmpty();
    }

}