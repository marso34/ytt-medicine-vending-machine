package com.example.ytt.domain.medicine.repository;

import com.example.ytt.domain.medicine.domain.MedicineIngredient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MedicineIngredientRepository extends JpaRepository<MedicineIngredient, Long> {

    List<MedicineIngredient> findByIngredientId(Long ingredientId); // 성분 ID로 검색 (특정 성분이 포함된 약품 목록 조회)

}
