package org.example.carpark.infrastructure.repository;

import org.example.carpark.application.dto.CarParkInfoResponse;
import org.example.carpark.domain.model.CarParkAvailability;
import org.example.carpark.domain.repository.CarParkAvailabilityRepository;
import org.example.carpark.infrastructure.persistence.JpaCarParkAvailabilityRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CarParkAvailabilityRepositoryImpl implements CarParkAvailabilityRepository {

    private final JpaCarParkAvailabilityRepository jpaCarParkAvailabilityRepository;

    public CarParkAvailabilityRepositoryImpl(JpaCarParkAvailabilityRepository jpaCarParkAvailabilityRepository) {
        this.jpaCarParkAvailabilityRepository = jpaCarParkAvailabilityRepository;
    }


    @Override
    public Page<CarParkInfoResponse> findAvailableCarParks(double latitude, double longitude, Pageable pageable) {
        return jpaCarParkAvailabilityRepository.findAvailableCarParks(latitude, longitude, pageable);
    }

    @Override
    public List<CarParkAvailability> saveAll(Iterable<CarParkAvailability> carParkAvailabilities) {
        return jpaCarParkAvailabilityRepository.saveAll(carParkAvailabilities);
    }

    @Override
    public List<CarParkAvailability> findAll() {
        return jpaCarParkAvailabilityRepository.findAll();
    }
}
