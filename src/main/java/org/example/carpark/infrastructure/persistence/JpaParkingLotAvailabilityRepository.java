package org.example.carpark.infrastructure.persistence;

import org.example.carpark.domain.model.CarParkAvailability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JpaParkingLotAvailabilityRepository extends JpaRepository<CarParkAvailability, String> {
    @Query("SELECT c FROM CarParkAvailability c WHERE c.carParkNo in :carParkIds")
    List<CarParkAvailability> findAllByCarParkNo(List<String> carParkIds);
}