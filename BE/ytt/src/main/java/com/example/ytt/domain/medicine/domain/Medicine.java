package com.example.ytt.domain.medicine.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "medicine")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = {"id", "productCode"})
@ToString(of = {"id", "name", "productCode", "manufacturer"})
public class Medicine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "medicine_id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "product_code", nullable = false, unique = true)
    private String productCode;

    @Column(name = "manufacturer", nullable = false)
    private String manufacturer;

    // EE, Efficacy and Effect (효능효과)
    @Column(name = "efficacy", columnDefinition = "TEXT", nullable = false)
    private String efficacy;

    // UD, Usage and Dosage (용법용량)
    @Column(name = "usages", columnDefinition = "TEXT", nullable = false)
    private String usages;

    // NB, Nota Bene (주의사항)
    @Column(name = "precautions", columnDefinition = "TEXT", nullable = false)
    private String precautions;

    // 유효기간
    @Column(name = "validity_period", nullable = false)
    private String validityPeriod;

    @Column(name = "image_url")
    private String imageURL;

    @Column(name = "price", nullable = false)
    private int price;

    @Setter
    @OneToMany(mappedBy = "medicine", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<MedicineIngredient> ingredients = new ArrayList<>();                   // 성분

    @Builder
    public Medicine(final String name, final String productCode, final String manufacturer, final String efficacy, final String usages, final String precautions, final String validityPeriod, final String imageURL, final int price) {
        Assert.hasText(name, "약품명은 필수입니다.");
        Assert.hasText(productCode, "제품코드는 필수입니다.");
        Assert.hasText(manufacturer, "제조사는 필수입니다.");
        Assert.hasText(efficacy, "효능효과는 필수입니다.");
        Assert.hasText(usages, "용법용량은 필수입니다.");
        Assert.hasText(precautions, "주의사항은 필수입니다.");
        Assert.hasText(validityPeriod, "유효기간은 필수입니다.");
        Assert.isTrue(price > 0, "가격은 0보다 커야합니다.");

        this.name = name;
        this.productCode = productCode;
        this.manufacturer = manufacturer;
        this.efficacy = efficacy;
        this.usages = usages;
        this.precautions = precautions;
        this.validityPeriod = validityPeriod;
        this.imageURL = imageURL;
        this.price = price;
    }

    public void addIngredient(Ingredient ingredient, int quantity, String unit) {
        addIngredient(MedicineIngredient.of(this, ingredient, quantity, unit));
    }

    public void addIngredient(Ingredient ingredient, int quantity, Unit unit) {
        addIngredient(MedicineIngredient.of(this, ingredient, quantity, unit));
    }

    public void addIngredient(MedicineIngredient medicineIngredient) {
        ingredients.add(medicineIngredient);
    }

    public void removeIngredient(Ingredient ingredient) {
        this.ingredients.removeIf(medicineIngredient -> medicineIngredient.getIngredient().equals(ingredient));
    }

    public Medicine setImageURL(String imageURL) {
        this.imageURL = imageURL;

        return this;
    }

    public Medicine setPrice(int price) {
        Assert.isTrue(price > 0, "가격은 0보다 커야합니다."); // 예외 처리 나중에
        this.price = price;

        return this;
    }
}
