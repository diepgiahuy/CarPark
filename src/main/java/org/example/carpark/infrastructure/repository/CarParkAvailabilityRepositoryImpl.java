package org.example.carpark.infrastructure.repository;

import org.example.carpark.infrastructure.persistence.JpaParkingLotAvailabilityRepository;
import org.example.carpark.domain.model.CarParkAvailability;
import org.example.carpark.domain.repository.CarParkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CarParkAvailabilityRepositoryImpl implements CarParkRepository {

    private final JpaParkingLotAvailabilityRepository jpaParkingLotAvailabilityRepository;


    @Autowired
    public CarParkAvailabilityRepositoryImpl(JpaParkingLotAvailabilityRepository jpaParkingLotAvailabilityRepository) {
        this.jpaParkingLotAvailabilityRepository = jpaParkingLotAvailabilityRepository;
    }
    @Override
    public List<CarParkAvailability> findAllByCarParkNoIn(List<String> carParkIds) {
        return jpaParkingLotAvailabilityRepository.findAllByCarParkNo(carParkIds);
    }

    @Override
    public List<CarParkAvailability> saveAll(Iterable<CarParkAvailability> carParkAvailabilities) {
        return jpaParkingLotAvailabilityRepository.saveAll(carParkAvailabilities);
    }
}
