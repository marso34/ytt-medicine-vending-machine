package com.example.ytt.domain.model;

import com.example.ytt.global.util.GeometryUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Point;

import static org.assertj.core.api.Assertions.assertThat;

class AddressTest {

    private Address address;
    private Point location;

    @BeforeEach
    void setUp() {
        location = GeometryUtil.createPoint(37.123456, 127.123456);

        address = Address.builder()
                .addressDetails("address")
                .location(location)
                .build();
    }

    @DisplayName("주소 생성 테스트")
    @Test
    void createAddress() {
        assertThat(address.getAddressDetails()).isEqualTo("address");
        assertThat(address.getLocation()).isEqualTo(location);
    }

    @DisplayName("주소 수정 테스트")
    @Test
    void updateAddress() {
        final Point newLocation = GeometryUtil.createPoint(37.654321, 127.654321);
        address.updateAddress("new address", newLocation);

        assertThat(address)
                .extracting("addressDetails", "location")
                .contains("new address", newLocation);
    }

    @DisplayName("Equals & HashCode 테스트")
    @Test
    void equalsAndHashCode() {
        final Address newAddress = Address.from("address", location);

        System.out.println(address.hashCode());

        assertThat(address).isEqualTo(newAddress);
        assertThat(address.hashCode()).hasSameHashCodeAs(newAddress.hashCode());
    }

    @DisplayName("ToString 테스트")
    @Test
    void toStringTest() {
        System.out.println(address.toString());

        assertThat(address.toString()).contains("addressDetails", "location");
    }
}