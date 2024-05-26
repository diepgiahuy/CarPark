package org.example.carpark.infrastructure.persistence;

import org.example.carpark.domain.model.CarParkInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaCarParkInfoRepository extends JpaRepository<CarParkInfo, String> {
}