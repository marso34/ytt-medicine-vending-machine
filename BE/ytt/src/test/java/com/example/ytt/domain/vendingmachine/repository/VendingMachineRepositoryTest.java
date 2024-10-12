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
        address = createAddress("address", 37.305121, 127.922653);
        sevedVendingMachine = createAndSaveVendingMachine("vendingMachine", address);
    }

    @DisplayName("자판기 저장 테스트")
    @Test
    void createVendingMachine() {
        assertVendingMachine(sevedVendingMachine, "vendingMachine", address, MachineState.OPERATING, 10);
    }

    @DisplayName("자판기 저장 테스트2")
    @Test
    void createVendingMachine2() {
        final VendingMachine vendingMachine = vendingMachineRepository.findById(sevedVendingMachine.getId()).orElse(null);

        assertVendingMachine(vendingMachine, "vendingMachine", address, MachineState.OPERATING, 10);
    }

    @DisplayName("자판기 이름으로 찾기 테스트")
    @Test
    void findByNameContaining() {
        final List<VendingMachine> vendingMachines = vendingMachineRepository.findByNameContaining("vending");

        assertThat(vendingMachines)
                .filteredOn(v -> v.getName().contains("vending"))
                .isNotNull();
    }

    @DisplayName("주변 위치 자판기 찾기 테스트")
    @Test
    void findNearByLocation() {
        // 자판기 좌표는 대략적인 좌표로 주석의 거리는 정확하지 않음
        createAndSaveVendingMachine("vendingMachine2", createAddress("address2", 37.307899, 127.920770)); // 약 500m
        createAndSaveVendingMachine("vendingMachine3", createAddress("address3", 37.311945, 127.927402)); // 약 1000m
        createAndSaveVendingMachine("vendingMachine4", createAddress("address4", 37.319237, 127.935006)); // 약 2500m

        // sevedVendingMachine의 주소로 검색해 항상 1개의 자판기가 검색되어야 함
        assertThat(vendingMachineRepository.findNearByLocation(address.getLocation(), 0)).hasSize(1);
        assertThat(vendingMachineRepository.findNearByLocation(address.getLocation(), 500)).hasSize(2);
        assertThat(vendingMachineRepository.findNearByLocation(address.getLocation(), 1100)).hasSize(3);
        assertThat(vendingMachineRepository.findNearByLocation(address.getLocation(), 2500)).hasSize(4);
    }

    // address 생성 메서드
    private Address createAddress(String details, double latitude, double longitude) {
        return Address.builder()
                .addressDetails(details)
                .location(GeometryUtil.createPoint(latitude, longitude))
                .build();
    }

    // vendingMachine 생성 메서드
    private VendingMachine createAndSaveVendingMachine(String name, Address address) {
        return vendingMachineRepository.save(VendingMachine.builder()
                .name(name)
                .address(address)
                .state(MachineState.OPERATING)
                .capacity(10)
                .build());
    }

    // assertions 메서드
    private void assertVendingMachine(VendingMachine vendingMachine, String name, Address address, MachineState state, int capacity) {
        assertThat(vendingMachine)
                .isNotNull()
                .extracting(VendingMachine::getName, VendingMachine::getAddress, VendingMachine::getState, VendingMachine::getCapacity)
                .containsExactly(name, address, state, capacity);
    }
}