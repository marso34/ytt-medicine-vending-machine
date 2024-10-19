package com.example.ytt.domain.medicine.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

@Entity
@Getter
@Table(name = "medicine_ingredient")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MedicineIngredient {

    // TODO: 복합키를 사용하는 것도 고려해볼 것

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "medicine_ingredient_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medicine_id", nullable = false)
    private Medicine medicine;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ingredient_id", nullable = false)
    private Ingredient ingredient;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Builder
    public MedicineIngredient(final Medicine medicine, final Ingredient ingredient, final Integer quantity) {
        Assert.notNull(medicine, "약은 필수입니다.");
        Assert.notNull(ingredient, "성분은 필수입니다.");
        Assert.notNull(quantity, "수량은 필수입니다.");

        this.medicine = medicine;
        this.ingredient = ingredient;
        this.quantity = quantity;
    }

    public static MedicineIngredient of(final Medicine medicine, final Ingredient ingredient, final Integer quantity) {
        return MedicineIngredient.builder()
                .medicine(medicine)
                .ingredient(ingredient)
                .quantity(quantity)
                .build();
    }
}
