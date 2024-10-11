package com.example.ytt.global.util;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

/**
 * Geometry 유틸리티 클래스
 */
public class GeometryUtil {

    // private 생성자: 인스턴스화를 방지
    private GeometryUtil() {
        throw new UnsupportedOperationException("Utility Class");
    }

    /**
     * 위도(latitude)와 경도(longitude)를 받아 Point 객체를 생성하여 반환
     * @param latitude 위도
     * @param longitude 경도
     * @return Point
     */
    public static Point createPoint(final Double latitude, final Double longitude) {
        final GeometryFactory geometryFactory = new GeometryFactory();
        Point point = geometryFactory.createPoint(new Coordinate(longitude, latitude));
        point.setSRID(4326);  // SRID를 4326으로 설정
        return point;
    }
}
