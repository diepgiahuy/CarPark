package org.example.carpark.infrastructure.persistence;

import org.example.carpark.domain.model.CarParkAvailability;
import org.example.carpark.domain.model.CarParkInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JpaCarParkInfoRepository extends JpaRepository<CarParkInfo, String> {
    @Query("SELECT c FROM CarParkInfo c WHERE c.carParkNo in :carParkNo")
    List<CarParkInfo> findAllByCarParkNo(List<String> carParkNo);
}