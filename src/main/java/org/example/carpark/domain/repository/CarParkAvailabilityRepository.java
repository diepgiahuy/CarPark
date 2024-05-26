package org.example.carpark.domain.repository;

import org.example.carpark.domain.model.CarParkAvailability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarParkAvailabilityRepository extends JpaRepository<CarParkAvailability, String> {
}