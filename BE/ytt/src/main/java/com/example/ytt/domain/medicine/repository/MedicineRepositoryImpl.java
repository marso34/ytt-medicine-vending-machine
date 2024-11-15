package com.example.ytt.domain.medicine.repository;

import com.example.ytt.domain.medicine.domain.Medicine;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.example.ytt.domain.medicine.domain.QIngredient.ingredient;
import static com.example.ytt.domain.medicine.domain.QMedicine.medicine;
import static com.example.ytt.domain.medicine.domain.QMedicineIngredient.medicineIngredient;

@Repository
@RequiredArgsConstructor
public class MedicineRepositoryImpl implements MedicineRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Medicine> getMedicines(String name, String manufacturer, String ingredientName) {
        return jpaQueryFactory
                .selectFrom(medicine)
                .leftJoin(medicine.ingredients, medicineIngredient)
                .leftJoin(medicineIngredient.ingredient, ingredient)
                .where(
                        nameContains(name),
                        manufacturerContains(manufacturer),
                        ingredientContains(ingredientName)
                )
                .fetch();
    }

    @Override
    public Optional<Medicine> getMedicineDetail(Long medicineId, String poductCode) {
        Medicine tMedicine = jpaQueryFactory
                .selectFrom(medicine)
                .leftJoin(medicine.ingredients, medicineIngredient).fetchJoin()
                .leftJoin(medicineIngredient.ingredient, ingredient).fetchJoin()
                .where(
                        equalsMedicineId(medicineId),
                        equalsProductCode(poductCode)
                )
                .fetchOne();

        return Optional.ofNullable(tMedicine);
    }

    private BooleanExpression nameContains(String name) {
        return name != null ? medicine.name.contains(name) : null;
    }

    private BooleanExpression manufacturerContains(String manufacturer) {
        return manufacturer != null ? medicine.manufacturer.contains(manufacturer) : null;
    }

    private BooleanExpression ingredientContains(String ingredientName) {
        return ingredientName != null ? medicine.ingredients.any().ingredient.name.contains(ingredientName) : null;
    }

    private BooleanExpression equalsMedicineId(Long medicineId) {
        return medicineId != null ? medicine.id.eq(medicineId) : null;
    }

    private BooleanExpression equalsProductCode(String productCode) {
        return productCode != null ? medicine.productCode.eq(productCode) : null;
    }


}
