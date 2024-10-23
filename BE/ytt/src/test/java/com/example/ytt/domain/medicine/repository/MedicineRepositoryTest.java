package com.example.ytt.domain.medicine.repository;

import com.example.ytt.domain.medicine.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class MedicineRepositoryTest {

    @Autowired
    private MedicineRepository medicineRepository;

    private Medicine sevedMedicine;
    private List<Ingredient> ingredients;

    @BeforeEach
    void setUp() {
        sevedMedicine = createAndSaveMedicine("medicine", "productCode");

        ingredients = List.of(
                Ingredient.builder().name("ingredient1").pharmacopeia(Pharmacopeia.BP).build(),
                Ingredient.builder().name("ingredient2").pharmacopeia(Pharmacopeia.KP).build()
        );
    }

    @DisplayName("약품 저장 테스트")
    @Test
    void createMedicine() {
        final Medicine medicine = medicineRepository.findById(sevedMedicine.getId()).orElse(null);

        assertMedicine(medicine, "medicine", "productCode");
    }

    @DisplayName("약품 이름으로 찾기 테스트")
    @Test
    void findByNameContaining() {
        final List<Medicine> medicines = medicineRepository.findByNameContaining("medicine");

        assertThat(medicines)
                .filteredOn(m -> m.getName().contains("medicine"))
                .isNotNull();
    }

    @DisplayName("약품 코드로 찾기 테스트")
    @Test
    void findByProductCode() {
        final Medicine medicine = medicineRepository.findByProductCode("productCode").orElse(null);

        assertMedicine(medicine, "medicine", "productCode");
    }

    @DisplayName("제조사로 찾기 테스트")
    @Test
    void findByManufacturer() {
        final List<Medicine> medicines = medicineRepository.findByManufacturer("manufacturer");

        assertThat(medicines)
                .filteredOn(m -> m.getManufacturer().equals("manufacturer"))
                .isNotNull();
    }

    // Medicine 생성 메서드
    private Medicine createAndSaveMedicine(String name, String productCode) {
        final Medicine medicine = Medicine.builder()
                .name(name)
                .productCode(productCode)
                .manufacturer("manufacturer")
                .efficacy("efficacy")
                .usage("usage")
                .precautions("precautions")
                .validityPeriod("validityPeriod")
                .price(1000)
                .build();

        return medicineRepository.save(medicine);
    }

    // assertions 메서드, name, productCode
    private void assertMedicine(Medicine medicine, String name, String productCode) {
        assertThat(medicine)
                .isNotNull()
                .extracting(Medicine::getName, Medicine::getProductCode)
                .containsExactly(name, productCode);
    }

}