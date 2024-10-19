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

    // TODO: e약은요 API, 의약품 제품 허가정보 중 기준 정할 것 (현재는 e약은요 API 기준)

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "medicine_id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;                                                                // 제품명

    @Column(name = "product_code", nullable = false)
    private String productCode;                                                         // 품목기준코드

    @Column(name = "manufacturer", nullable = false)
    private String manufacturer;                                                        // 제조사

    @Column(name = "efficacy", columnDefinition = "TEXT", nullable = false)
    private String efficacy;                                                            // 효능 (이 약의 효능은 무엇입니까?) or 효능효과

    @Column(name = "usage_instructions", columnDefinition = "TEXT", nullable = false)
    private String usageInstructions;                                                   // 사용법 (이 약은 어떻게 사용합니까?) or 용법용량

    @Column(name = "warnings", columnDefinition = "TEXT")
    private String warnings;                                                            // 주의사항 경고 (이 약을 사용하기 전에 반드시 알아야 할 내용은 무엇입니까?)

    @Column(name = "precautions", columnDefinition = "TEXT", nullable = false)
    private String precautions;                                                         // 주의사항 (이 약의 사용상 주의사항은 무엇입니까?) or 사용상의 주의사항

    @Column(name = "interactions", columnDefinition = "TEXT")
    private String interactions;                                                        // 상호작용 (이 약을 사용하는 동안 주의해야 할 약 또는 음식은 무엇입니까?)

    @Column(name = "side_effects", columnDefinition = "TEXT", nullable = false)
    private String sideEffects;                                                         // 부작용 (이 약은 어떤 이상반응이 나타날 수 있습니까?)

    @Column(name = "storage_instructions", columnDefinition = "TEXT")
    private String storageInstructions;                                                 // 보관법 (이 약은 어떻게 보관해야 합니까?)

//    @Column(name = "expiration_date", nullable = false)
//    private String expirationDate;                                                     // 유효기간, e약은요 API에 정보 없음, 의약품 제품 허가정보에는 있음

    @Column(name = "price", nullable = false)
    private Integer price;                                                              // 가격

    @Column(name = "image_url")
    private String imageUrl;                                                               // 이미지

    @OneToMany(mappedBy = "medicine", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<MedicineIngredient> ingredients;

    @Builder
    public Medicine(String name, String productCode, String manufacturer, String efficacy, String usageInstructions, String warnings, String precautions, String interactions, String sideEffects, String storageInstructions, Integer price, String imageUrl, List<MedicineIngredient> ingredients) {
        Assert.hasText(name, "약 이름은 필수입니다.");
        Assert.hasText(productCode, "약 코드는 필수입니다.");
        Assert.hasText(manufacturer, "제조사는 필수입니다.");
        Assert.hasText(efficacy, "효능은 필수입니다.");
        Assert.hasText(usageInstructions, "사용법은 필수입니다.");
        Assert.hasText(precautions, "주의사항은 필수입니다.");
        Assert.hasText(sideEffects, "부작용은 필수입니다.");
        Assert.notNull(price, "가격은 필수입니다.");

//        Assert.notNull(warnings, "주의사항 경고는 필수입니다.");
//        Assert.notNull(interactions, "상호작용은 필수입니다.");
//        Assert.notNull(storageInstructions, "보관법은 필수입니다.");
//        Assert.notNull(imageUrl, "이미지는 필수입니다.");
//        Assert.notNull(ingredients, "성분은 필수입니다.");

        this.name = name;
        this.productCode = productCode;
        this.manufacturer = manufacturer;
        this.efficacy = efficacy;
        this.usageInstructions = usageInstructions;
        this.warnings = warnings;
        this.precautions = precautions;
        this.interactions = interactions;
        this.sideEffects = sideEffects;
        this.storageInstructions = storageInstructions;
        this.price = price;
        this.imageUrl = imageUrl;
        this.ingredients = ingredients == null ? new ArrayList<>() : ingredients;
    }

    public void addIngredient(Ingredient ingredient) {
        addIngredient(ingredient, 1);
    }

    public void addIngredient(Ingredient ingredient, Integer quantity) {
        this.ingredients.add(MedicineIngredient.of(this, ingredient, quantity));
    }

    public void removeIngredient(Ingredient ingredient) {
        this.ingredients.removeIf(medicineIngredient -> medicineIngredient.getIngredient().equals(ingredient));
    }
}
