package org.example.carpark.domain.repository;

import org.example.carpark.domain.model.CarParkAvailability;

import java.util.List;

public interface CarParkRepository {
    List<CarParkAvailability> findAllByCarParkNoIn(List<String> carParkNo);
    List<CarParkAvailability> saveAll(Iterable<CarParkAvailability> carParkAvailabilities);

}

