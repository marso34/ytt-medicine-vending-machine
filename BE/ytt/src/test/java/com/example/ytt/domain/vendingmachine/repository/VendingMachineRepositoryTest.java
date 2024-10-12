package com.example.ytt.domain.vendingmachine.repository;

import com.example.ytt.domain.model.Address;
import com.example.ytt.domain.vendingmachine.domain.MachineState;
import com.example.ytt.domain.vendingmachine.domain.VendingMachine;
import com.example.ytt.global.util.GeometryUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class VendingMachineRepositoryTest {

    @Autowired
    private VendingMachineRepository vendingMachineRepository;

    private VendingMachine sevedVendingMachine;
    private Address address;

    @BeforeEach
    void setUp() {
        address = Address.builder()
                .addressDetails("address")
                .location(GeometryUtil.createPoint(37.123456, 127.123456))
                .build();

        sevedVendingMachine = vendingMachineRepository.save(VendingMachine.builder()
                .name("vendingMachine")
                .address(address)
                .state(MachineState.OPERATING)
                .capacity(10)
                .build());
    }

    @DisplayName("자판기 생성 테스트")
    @Test
    void createVendingMachine() {
        assertThat(sevedVendingMachine).isNotNull();
        assertThat(sevedVendingMachine.getName()).isEqualTo("vendingMachine");
        assertThat(sevedVendingMachine.getAddress()).isEqualTo(address);
        assertThat(sevedVendingMachine.getState()).isEqualTo(MachineState.OPERATING);
        assertThat(sevedVendingMachine.getCapacity()).isEqualTo(10);
    }

    @DisplayName("자판기 생성 테스트2")
    @Test
    void createVendingMachine2() {
        final VendingMachine vendingMachine = vendingMachineRepository.findById(sevedVendingMachine.getId()).orElse(null);

        assertThat(vendingMachine).isNotNull();
        assertThat(vendingMachine.getName()).isEqualTo("vendingMachine");
        assertThat(vendingMachine.getAddress()).isEqualTo(address);
        assertThat(vendingMachine.getState()).isEqualTo(MachineState.OPERATING);
        assertThat(vendingMachine.getCapacity()).isEqualTo(10);
    }

    @DisplayName("자판기 이름으로 찾기 테스트")
    @Test
    void findByNameContaining() {
        final List<VendingMachine> vendingMachine = vendingMachineRepository.findByNameContaining("vending");

        assertThat(vendingMachine).isNotNull();
        assertThat(vendingMachine.get(0).getName()).contains("vending");
    }

}