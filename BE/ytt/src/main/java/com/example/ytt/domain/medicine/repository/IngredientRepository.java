package com.example.ytt.domain.medicine.repository;

import com.example.ytt.domain.medicine.domain.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IngredientRepository extends JpaRepository<Ingredient, Long> {
}
