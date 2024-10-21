package com.example.ytt.domain.vendingmachine.domain;

import com.example.ytt.domain.model.Address;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class VendingMachineTest {

    private VendingMachine vendingMachine;
    private Address address;

    @BeforeEach
    void setUp() {
         address = Address.of("address", 37.123456, 127.123456);

        vendingMachine = VendingMachine.builder()
                .name("name")
                .address(address)
                .state(MachineState.OPERATING)
                .capacity(10)
                .build();
    }

    @DisplayName("자판기 생성 테스트")
    @Test
    void createVendingMachine() {
        assertThat(vendingMachine).isNotNull();
        assertThat(vendingMachine.getName()).isEqualTo("name");
        assertThat(vendingMachine.getAddress()).isEqualTo(address);
        assertThat(vendingMachine.getState()).isEqualTo(MachineState.OPERATING);
        assertThat(vendingMachine.getCapacity()).isEqualTo(10);
    }

    @DisplayName("자판기 이름 수정 테스트")
    @Test
    void updateVendingMachineName() {
        vendingMachine.updateName("new name");

        assertThat(vendingMachine.getName()).isEqualTo("new name");
    }

    @DisplayName("자판기 주소 수정 테스트")
    @Test
    void updateVendingMachineAddress() {
        final Address newAddress = Address.of("new address", 37.654321, 127.654321);

        vendingMachine.updateAddress(newAddress);

        assertThat(vendingMachine.getAddress()).isEqualTo(newAddress);
    }

    @DisplayName("자판기 상태 수정 테스트")
    @Test
    void updateVendingMachineState() {
        vendingMachine.updateState(MachineState.MAINTENANCE);

        assertThat(vendingMachine.getState()).isEqualTo(MachineState.MAINTENANCE);
    }

    @DisplayName("Equals & HashCode 테스트")
    @Test
    void equalsAndHashCode() {
        final VendingMachine vendingMachine2 = VendingMachine.builder()
                .name("name")
                .address(address)
                .state(MachineState.OPERATING)
                .capacity(10)
                .build();

        System.out.println(vendingMachine.hashCode());

        assertThat(vendingMachine).isEqualTo(vendingMachine2);
        assertThat(vendingMachine.hashCode()).hasSameHashCodeAs(vendingMachine2.hashCode());
    }

    @DisplayName("ToString 테스트")
    @Test
    void toStringTest() {
        System.out.println(vendingMachine.toString());

        assertThat(vendingMachine.toString()).contains("name", "address", "state", "capacity");
    }

}