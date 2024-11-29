package com.example.ytt.domain.medicine.repository;

import com.example.ytt.domain.medicine.domain.Medicine;

import java.util.List;
import java.util.Optional;

public interface MedicineRepositoryCustom {

    List<Medicine> getMedicines(String name, String manufacturer, String ingredientName);

    List<Medicine> getMedicines(List<Long> medicineId, List<String> poductCode);

    Optional<Medicine> getMedicineDetail(Long medicineId, String poductCode);

}
