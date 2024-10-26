package com.example.ytt.domain.medicine.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.util.Assert;

@Entity
@Getter
@Table(name = "medicine_ingredient")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = {"id", "medicine", "ingredient", "quantity", "unit"})
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
    private double quantity;

    @Enumerated(EnumType.STRING)
    private Unit unit;

    @Builder
    public MedicineIngredient(final Medicine medicine, final Ingredient ingredient, final double quantity, final Unit unit) {
        Assert.notNull(medicine, "약은 필수입니다.");
        Assert.notNull(ingredient, "성분은 필수입니다.");
        Assert.isTrue(quantity > 0, "수량은 0보다 커야합니다.");
        Assert.notNull(unit, "단위는 필수입니다.");

        this.medicine = medicine;
        this.ingredient = ingredient;
        this.quantity = quantity;
        this.unit = unit;
    }

    public static MedicineIngredient of(final Medicine medicine, final Ingredient ingredient, final double quantity, final Unit unit) {
        return MedicineIngredient.builder()
                .medicine(medicine)
                .ingredient(ingredient)
                .quantity(quantity)
                .unit(unit)
                .build();
    }

    public static MedicineIngredient of(final Medicine medicine, final Ingredient ingredient, final double quantity, final String unit) {
        return of(medicine, ingredient, quantity, Unit.from(unit));
    }
}
