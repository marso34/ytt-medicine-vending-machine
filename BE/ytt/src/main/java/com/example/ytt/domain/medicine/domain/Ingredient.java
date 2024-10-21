package com.example.ytt.domain.medicine.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.util.Assert;

@Entity
@Getter
@Table(name = "ingredient")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = {"id", "name"})
@ToString(of = {"name"})
public class Ingredient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ingredient_id", nullable = false)
    private Long id;

    @Column(name = "ingredient_name", nullable = false)
    private String name; // 성분명

    @Setter
    @Column(name = "efficacy", columnDefinition = "TEXT")
    private String efficacy; // 효능

    @Builder
    public Ingredient(final String name, final String efficacy) {
        Assert.hasText(name, "성분명은 필수입니다.");
//        Assert.hasText(efficacy, "효능은 필수입니다.");

        this.name = name;
        this.efficacy = efficacy;
    }

    public static Ingredient of(final String name) {
        return of(name, null);
    }

    public static Ingredient of(final String name, final String efficacy) {
        return Ingredient.builder()
                .name(name)
                .efficacy(efficacy)
                .build();
    }
}
