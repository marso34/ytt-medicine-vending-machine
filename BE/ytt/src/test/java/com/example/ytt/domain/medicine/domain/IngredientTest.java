package com.example.ytt.domain.medicine.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class IngredientTest {

    private Ingredient ingredient;

    @BeforeEach
    void setUp() {
        ingredient = Ingredient.of("ingredient", "efficacy", "KP");
    }

    @DisplayName("성분 생성 테스트")
    @Test
    void createIngredient() {
        assertThat(ingredient).isNotNull();
        assertThat(ingredient.getName()).isEqualTo("ingredient");
        assertThat(ingredient.getEfficacy()).isEqualTo("efficacy");
    }

}