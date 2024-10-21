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
    private int quantity;

    @Enumerated(EnumType.STRING)
    private Unit unit;

    @Enumerated(EnumType.STRING)
    private Pharmacopeia pharmacopeia;

    @Builder
    public MedicineIngredient(final Medicine medicine, final Ingredient ingredient, final int quantity, final Unit unit, final Pharmacopeia pharmacopeia) {
        Assert.notNull(medicine, "약은 필수입니다.");
        Assert.notNull(ingredient, "성분은 필수입니다.");
        Assert.isTrue(quantity > 0, "수량은 0보다 커야합니다.");
        Assert.notNull(unit, "단위는 필수입니다.");
        Assert.notNull(pharmacopeia, "약전은 필수입니다.");

        this.medicine = medicine;
        this.ingredient = ingredient;
        this.quantity = quantity;
        this.unit = unit;
        this.pharmacopeia = pharmacopeia;
    }

    public static MedicineIngredient of(final Medicine medicine, final Ingredient ingredient, final int quantity, final Unit unit, final Pharmacopeia pharmacopeia) {
        return MedicineIngredient.builder()
                .medicine(medicine)
                .ingredient(ingredient)
                .quantity(quantity)
                .unit(unit)
                .pharmacopeia(pharmacopeia)
                .build();
    }

    public static MedicineIngredient of(final Medicine medicine, final Ingredient ingredient, final int quantity, final String unit, final String pharmacopeia) {
        return of(medicine, ingredient, quantity, Unit.from(unit), Pharmacopeia.from(pharmacopeia));
    }
}
