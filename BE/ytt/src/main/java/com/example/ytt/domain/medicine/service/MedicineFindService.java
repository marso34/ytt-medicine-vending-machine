package com.example.ytt.domain.medicine.service;

import com.example.ytt.domain.medicine.domain.Medicine;
import com.example.ytt.domain.medicine.domain.MedicineIngredient;
import com.example.ytt.domain.medicine.dto.IngredientDto;
import com.example.ytt.domain.medicine.dto.MedicineDetailDto;
import com.example.ytt.domain.medicine.dto.MedicineDto;
import com.example.ytt.domain.medicine.repository.IngredientRepository;
import com.example.ytt.domain.medicine.repository.MedicineIngredientRepository;
import com.example.ytt.domain.medicine.repository.MedicineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MedicineFindService {

    private final MedicineRepository medicineRepository;
    private final IngredientRepository ingredientRepository;
    private final MedicineIngredientRepository medicineIngredientRepository;

    // TODO: 예외 처리는 나중에

    // MedicineDto 리스트 조회

    public List<MedicineDto> getAllMedicines () {
        return medicineRepository.findAll()
                .stream()
                .map(MedicineDto::from)
                .toList();
    }

    public List<MedicineDto> getMedicinesByName(String name) {
        return medicineRepository.findByNameContaining(name)
                .stream()
                .map(MedicineDto::from)
                .toList();
    }

    public List<MedicineDto> getMedicinesByManufacturer(String manufacturer) {
        return medicineRepository.findByManufacturer(manufacturer)
                .stream()
                .map(MedicineDto::from)
                .toList();
    }

    // 특정 성분이 포함된 약품 목록 조회 (성분 ID로 검색)
    public List<MedicineDto> findMedicineByIngredientId(Long ingredientId) {
        // Ingredient가 존재하는 지 확인
        if (!ingredientRepository.existsById(ingredientId)) {
            throw new IllegalArgumentException("Ingredient not found"); // 나중에 예외 처리 직접 할 것
        }

        // MedicineIngredient에서 ingredientId로 검색하여 medicineId를 가져옴
        List<MedicineIngredient> medicineIngredients = medicineIngredientRepository.findByIngredientId(ingredientId);

        return medicineIngredients.stream()
                .map(medicineIngredient -> MedicineDto.from(medicineIngredient.getMedicine()))
                .toList();
    }

    // MedicineDetailDto 조회

    public MedicineDetailDto getMedicineById(Long id) {
        Medicine medicine = medicineRepository.findById(id).orElse(null);

        if (medicine == null) {
            return null;
        }

        List<IngredientDto> ingredients = medicine.getIngredients().stream()
                .map(IngredientDto::from)
                .toList();

        return MedicineDetailDto.of(medicine, ingredients);
    }

    public MedicineDetailDto getMedicinesByProductCode(String productCode) {
        Medicine medicine = medicineRepository.findByProductCode(productCode).orElse(null);

        if (medicine == null) {
            return null;
        }

        List<IngredientDto> ingredients = medicine.getIngredients().stream()
                .map(IngredientDto::from)
                .toList();

        return MedicineDetailDto.of(medicine, ingredients);
    }

}
