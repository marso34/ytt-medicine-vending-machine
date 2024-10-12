package com.example.ytt.domain.vendingmachine.repository;

import com.example.ytt.domain.vendingmachine.domain.VendingMachine;
import org.locationtech.jts.geom.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VendingMachineRepository extends JpaRepository<VendingMachine, Long> {
    List<VendingMachine> findByNameContaining(String name);

//    ST_DISTANCE_SPHERE 미지원으로 인해 ST_DISTANCE ST_TRANSFORM(3857, 단위를 1m로)으로 구현
    /**
     * 지정된 위치에서 지정된 거리 이내의 자판기 목록을 조회
     * @param location Point(경도, 위도)
     * @param distance 거리(단위: m)
     * @return List
     */
    @Query("""
            SELECT v
            FROM VendingMachine v
            WHERE ST_DISTANCE(ST_TRANSFORM(v.address.location, 3857), ST_TRANSFORM(:location, 3857)) <= :distance
            """)
    List<VendingMachine> findNearByLocation(@Param("location") final Point location, @Param("distance") final double distance);
}
