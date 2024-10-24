package com.example.ytt.domain.medicine.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.util.Assert;

@Entity
@Getter
@Table(name = "ingredient")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = {"id", "name"})
@ToString(of = {"name", "pharmacopeia"})
public class Ingredient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ingredient_id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name; // 성분명

    @Setter
    @Column(name = "efficacy", columnDefinition = "TEXT")
    private String efficacy; // 효능

    @Enumerated(EnumType.STRING)
    @Column(name = "pharmacopeia", nullable = false)
    private Pharmacopeia pharmacopeia;

    @Builder
    public Ingredient(final String name, final String efficacy, final Pharmacopeia pharmacopeia) {
        Assert.hasText(name, "성분명은 필수입니다.");
        Assert.notNull(pharmacopeia, "약전은 필수입니다.");

        this.name = name;
        this.efficacy = efficacy;
        this.pharmacopeia = pharmacopeia;
    }

    public static Ingredient of(final String name, final Pharmacopeia pharmacopeia) {
        return of(name, null, pharmacopeia);
    }

    public static Ingredient of(final String name, final String efficacy, final String pharmacopeia) {
        return of(name, efficacy, Pharmacopeia.from(pharmacopeia));
    }

    public static Ingredient of(final String name, final String efficacy, final Pharmacopeia pharmacopeia) {
        return Ingredient.builder()
                .name(name)
                .efficacy(efficacy)
                .pharmacopeia(pharmacopeia)
                .build();
    }
}
