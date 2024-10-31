package com.example.ytt.domain.medicine.service;

import com.example.ytt.domain.medicine.domain.*;
import com.example.ytt.domain.medicine.dto.IngredientDto;
import com.example.ytt.domain.medicine.dto.MedicineDetailDto;
import com.example.ytt.domain.medicine.dto.MedicineDto;
import com.example.ytt.domain.medicine.repository.IngredientRepository;
import com.example.ytt.domain.medicine.repository.MedicineIngredientRepository;
import com.example.ytt.domain.medicine.repository.MedicineRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class MedicineFindServiceTest {

    @InjectMocks
    MedicineFindService medicineFindService;

    @Mock
    MedicineRepository medicineRepository;

    @Mock
    IngredientRepository ingredientRepository;

    @Mock
    MedicineIngredientRepository medicineIngredientRepository;

    List<Medicine> medicines;
    List<Ingredient> ingredients;
    List<MedicineIngredient> medicineIngredients;

    @BeforeEach
    void setUp() {
        medicines = List.of(
                createMedicine("medicine1", "productCode1"),
                createMedicine("medicine2", "productCode2"),
                createMedicine("medicine3", "productCode3")
        );

        ingredients = List.of(
                Ingredient.of("ingredient1", Pharmacopeia.BP),
                Ingredient.of("ingredient2", Pharmacopeia.KP)
        );

        medicineIngredients = List.of(
                MedicineIngredient.of(medicines.get(0), ingredients.get(0), 10, Unit.MG),
                MedicineIngredient.of(medicines.get(0), ingredients.get(1), 20, Unit.MG),
                MedicineIngredient.of(medicines.get(1), ingredients.get(0), 30, Unit.MG),
                MedicineIngredient.of(medicines.get(1), ingredients.get(1), 40, Unit.MG),
                MedicineIngredient.of(medicines.get(2), ingredients.get(0), 50, Unit.MG),
                MedicineIngredient.of(medicines.get(2), ingredients.get(1), 60, Unit.MG)
        );


        medicines.get(0).setIngredients(List.of(medicineIngredients.get(0), medicineIngredients.get(1)));
        medicines.get(1).setIngredients(List.of(medicineIngredients.get(2), medicineIngredients.get(3)));
        medicines.get(2).setIngredients(List.of(medicineIngredients.get(4), medicineIngredients.get(5)));

    }

    @DisplayName("모든 약품 조회 테스트")
    @Test
    void getAllMedicines() {
        // given
        given(medicineRepository.findAll()).willReturn(medicines);

        // when
        List<MedicineDto> medicineDtos = medicineFindService.getAllMedicines();

        // then
        assertThat(medicineDtos)
                .isNotEmpty()
                .isEqualTo(medicines.stream().map(MedicineDto::from).toList());
    }

    @DisplayName("약품 이름으로 조회 테스트")
    @Test
    void getMedicinesByName() {
        // given
        List<Medicine> filteredMedicines = medicines.stream().filter(m -> m.getName().contains("medicine1")).toList();

        given(medicineRepository.findByNameContaining("medicine1")).willReturn(filteredMedicines);

        // when
        List<MedicineDto> medicineDtos = medicineFindService.getMedicinesByName("medicine1");

        // then
        assertThat(medicineDtos)
                .isNotEmpty()
                .isEqualTo(filteredMedicines.stream().map(MedicineDto::from).toList());
    }

    @DisplayName("제조사로 약품 조회 테스트")
    @Test
    void getMedicinesByManufacturer() {
        // given
        List<Medicine> filteredMedicines = medicines.stream().filter(m -> m.getManufacturer().contains("manufacturer")).toList();

        given(medicineRepository.findByManufacturer("manufacturer")).willReturn(filteredMedicines);

        // when
        List<MedicineDto> medicineDtos = medicineFindService.getMedicinesByManufacturer("manufacturer");

        // then
        assertThat(medicineDtos)
                .isNotEmpty()
                .isEqualTo(filteredMedicines.stream().map(MedicineDto::from).toList());
    }

    @DisplayName("특정 성분이 포함된 약품 목록 조회 테스트")
    @Test
    void findMedicineByIngredientId() {
        // given
        Long ingredientId = 1L;

        List<MedicineIngredient> filteredMedicineIngredients = medicineIngredients.stream().filter(m -> m.getIngredient().getName().equals("ingredient1")).toList();

        given(ingredientRepository.existsById(ingredientId)).willReturn(true);
        given(medicineIngredientRepository.findByIngredientId(ingredientId)).willReturn(filteredMedicineIngredients);

        // when
        List<MedicineDto> medicineDtos = medicineFindService.findMedicineByIngredientId(ingredientId);

        // then
        assertThat(medicineDtos)
                .isNotEmpty()
                .isEqualTo(filteredMedicineIngredients.stream().map(medicineIngredient -> MedicineDto.from(medicineIngredient.getMedicine())).toList());
    }

    @DisplayName("약품 상세 조회 테스트")
    @Test
    void getMedicineById() {
        // given
        Medicine medicine = medicines.get(0);

        given(medicineRepository.findById(1L)).willReturn(java.util.Optional.of(medicine));

        // when
        MedicineDetailDto medicineDetailDto = medicineFindService.getMedicineById(1L);

        // then
        assertThat(medicineDetailDto)
                .isNotNull()
                .isEqualTo(MedicineDetailDto.of(medicine, medicine.getIngredients().stream().map(IngredientDto::from).toList()));
    }

    @DisplayName("제품코드로 약품 조회 테스트")
    @Test
    void getMedicinesByProductCode() {
        // given
        String productCode = "productCode2";

        Medicine medicine = medicines.get(1);

        given(medicineRepository.findByProductCode(productCode)).willReturn(java.util.Optional.ofNullable(medicine));

        // when
        MedicineDetailDto medicineDetailDto = medicineFindService.getMedicinesByProductCode(productCode);

        // then
        assertThat(medicineDetailDto)
                .isNotNull()
                .isEqualTo(MedicineDetailDto.of(medicine, medicine.getIngredients().stream().map(IngredientDto::from).toList()));
    }


    // Medicine 생성 메서드
    private Medicine createMedicine(String name, String productCode) {
        return  Medicine.builder()
                .name(name)
                .productCode(productCode)
                .manufacturer("manufacturer")
                .efficacy("efficacy")
                .usages("usage")
                .precautions("precautions")
                .validityPeriod("validityPeriod")
                .price(1000)
                .build();
    }

}