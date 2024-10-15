package com.example.ytt.domain.model;

import com.example.ytt.global.util.GeometryUtil;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import org.locationtech.jts.geom.Point;
import org.springframework.util.Assert;

@Embeddable
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Getter
@EqualsAndHashCode(of = {"addressDetails", "location"})
@ToString(of = {"addressDetails", "location"})
public class Address {

        @NotEmpty
        @Column(name = "address")
        private String addressDetails;

        @NotEmpty
        @Column(name = "location", columnDefinition = "POINT SRID 4326")
        private Point location;

        @Builder
        public Address(String addressDetails, Point location) {
            Assert.hasText(addressDetails, "주소는 필수입니다.");
            Assert.notNull(location, "위치는 필수입니다.");

            this.addressDetails = addressDetails;
            this.location = location;
        }

        public static Address from(String addressDetails, double latitude, double longitude) {
            return from(addressDetails, GeometryUtil.createPoint(latitude, longitude));
        }

        public static Address from(String addressDetails, Point location) {
            return Address.builder()
                    .addressDetails(addressDetails)
                    .location(location)
                    .build();
        }

        public void updateAddress(final String addressDetails, final Point location) {
            this.addressDetails = addressDetails;
            this.location = location;
        }

}
