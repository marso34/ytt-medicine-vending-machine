package com.example.ytt.domain.medicine.repository;

import com.example.ytt.domain.medicine.domain.Medicine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MedicineRepository extends JpaRepository<Medicine, Long> {

    Optional<Medicine> findByProductCode(String productCode);

    List<Medicine> findByNameContaining(String name);

    List<Medicine> findByManufacturer(String manufacturer);

}
