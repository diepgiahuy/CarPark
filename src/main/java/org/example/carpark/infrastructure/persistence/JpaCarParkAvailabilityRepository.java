package org.example.carpark.infrastructure.persistence;

import org.example.carpark.application.dto.CarParkInfoResponse;
import org.example.carpark.domain.model.CarParkAvailability;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaCarParkAvailabilityRepository extends JpaRepository<CarParkAvailability, String> {
    @Query(value = "SELECT cpi.address AS address, cpi.latitude AS latitude, cpi.longitude AS longitude, " +
            "cpa.total_lots AS totalLots, cpa.available_lots AS availableLots, " +
            "(6371 * acos(cos(radians(:latitude)) * cos(radians(cpi.latitude)) * " +
            "cos(radians(cpi.longitude) - radians(:longitude)) + sin(radians(:latitude)) * " +
            "sin(radians(cpi.latitude)))) AS distance " +
            "FROM car_park_availability cpa " +
            "JOIN car_park_info cpi ON cpa.car_park_no = cpi.car_park_no " +
            "WHERE cpa.available_lots > 0 " +
            "ORDER BY distance ASC",
            countQuery = "SELECT count(*) " +
                    "FROM car_park_availability cpa " +
                    "JOIN car_park_info cpi ON cpa.car_park_no = cpi.car_park_no " +
                    "WHERE cpa.available_lots > 0",
            nativeQuery = true)
    Page<CarParkInfoResponse> findAvailableCarParks(@Param("latitude") double latitude, @Param("longitude") double longitude, Pageable pageable);
}