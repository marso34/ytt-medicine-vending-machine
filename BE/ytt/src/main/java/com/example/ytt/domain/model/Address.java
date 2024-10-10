package com.example.ytt.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import org.locationtech.jts.geom.Point;

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
            this.addressDetails = addressDetails;
            this.location = location;
        }

        public void updateAddress(final String addressDetails, final Point location) {
            this.addressDetails = addressDetails;
            this.location = location;
        }

}
