package com.example.ytt.domain.medicine.service;

import com.example.ytt.domain.medicine.domain.Medicine;
import com.example.ytt.domain.medicine.domain.MedicineIngredient;
import com.example.ytt.domain.medicine.dto.MedicineDetailDto;
import com.example.ytt.domain.medicine.dto.MedicineDto;
import com.example.ytt.domain.medicine.exception.IngredientException;
import com.example.ytt.domain.medicine.exception.MedicineException;
import com.example.ytt.domain.medicine.repository.IngredientRepository;
import com.example.ytt.domain.medicine.repository.MedicineIngredientRepository;
import com.example.ytt.domain.medicine.repository.MedicineRepository;
import com.example.ytt.global.error.code.ExceptionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MedicineFindService {

    private final MedicineRepository medicineRepository;
    private final IngredientRepository ingredientRepository;
    private final MedicineIngredientRepository medicineIngredientRepository;

    // MedicineDto 리스트 조회

    public List<MedicineDto> getAllMedicines () {
        List<Medicine> list = medicineRepository.findAll();

        if (list.isEmpty()) {
            throw new MedicineException(ExceptionType.NO_CONTENT_MEDICINE);
        }

        return list.stream().map(MedicineDto::from).toList();
    }

    /**
     * @deprecated 추후 QueryDSL로 구현한 코드로 대체 예정
     */
    @Deprecated(since="", forRemoval=true)
    public List<MedicineDto> getMedicinesByName(String name) {
        List<Medicine> list = medicineRepository.findByNameContaining(name);

        if (list.isEmpty()) {
            throw new MedicineException(ExceptionType.NO_CONTENT_MEDICINE);
        }

        return list.stream().map(MedicineDto::from).toList();
    }

    /**
     * @deprecated 추후 QueryDSL로 구현한 코드로 대체 예정
     */
    @Deprecated(since="", forRemoval=true)
    public List<MedicineDto> getMedicinesByManufacturer(String manufacturer) {
        List<Medicine> list = medicineRepository.findByManufacturer(manufacturer);

        if (list.isEmpty()) {
            throw new MedicineException(ExceptionType.NO_CONTENT_MEDICINE);
        }

        return list.stream().map(MedicineDto::from).toList();
    }

    // 특정 성분이 포함된 약품 목록 조회 (성분 ID로 검색)
    /**
     * @deprecated 추후 QueryDSL로 구현한 코드로 대체 예정
     */
    @Deprecated(since="", forRemoval=true)
    public List<MedicineDto> findMedicineByIngredientId(Long ingredientId) {
        if (!ingredientRepository.existsById(ingredientId)) {
            throw new IngredientException(ExceptionType.NOT_FOUND_INGREDIENT); // Ingredient가 존재하는 지 확인
        }

        List<MedicineIngredient> list = medicineIngredientRepository.findByIngredientId(ingredientId);

        if (list.isEmpty()) {
            throw new MedicineException(ExceptionType.NO_CONTENT_INGREDIENT); // 해당 성분을 포함하는 약품이 없을 경우
        }

        return list.stream().map(MedicineDto::from).toList();
    }

    // MedicineDetailDto 조회

    /**
     * @deprecated 추후 QueryDSL로 구현한 코드로 대체 예정
     */
    @Deprecated(since="", forRemoval=true)
    public MedicineDetailDto getMedicineById(Long id) {
        Medicine medicine = medicineRepository.findById(id).orElseThrow(() -> new MedicineException(ExceptionType.NOT_FOUND_MEDICINE));

        return MedicineDetailDto.from(medicine);
    }

    /**
     * @deprecated 추후 QueryDSL로 구현한 코드로 대체 예정
     */
    @Deprecated(since="", forRemoval=true)
    public MedicineDetailDto getMedicinesByProductCode(String productCode) {
        Medicine medicine = medicineRepository.findByProductCode(productCode).orElseThrow(() -> new MedicineException(ExceptionType.NOT_FOUND_MEDICINE));

        return MedicineDetailDto.from(medicine);
    }

    /* -- QueryDSL을 사용한 코드 -- */

    public List<MedicineDto> getMedicines(String name, String manufacturer, Long ingredientId) {
        List<Medicine> list = medicineRepository.getMedicines(name, manufacturer, ingredientId);

        if (list.isEmpty()) {
            throw new MedicineException(ExceptionType.NO_CONTENT_MEDICINE);
        }

        return list.stream().map(MedicineDto::from).toList();
    }

    public MedicineDetailDto getMedicineDetail(Long medicineId, String productCode) {
        if (medicineId == null && productCode == null) {
            throw new MedicineException(ExceptionType.BAD_REQUEST_MEDICINE_SEARCH);
        }

        Medicine medicine = medicineRepository.getMedicineDetail(medicineId, productCode).orElseThrow(() -> new MedicineException(ExceptionType.NOT_FOUND_MEDICINE));

        return MedicineDetailDto.from(medicine);
    }

}
