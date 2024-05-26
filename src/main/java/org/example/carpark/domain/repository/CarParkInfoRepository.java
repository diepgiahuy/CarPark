package org.example.carpark.domain.repository;

import org.example.carpark.domain.model.CarParkAvailability;
import org.example.carpark.domain.model.CarParkInfo;

import java.util.List;

public interface CarParkInfoRepository {
    List<CarParkInfo> findAllByCarParkNoIn(List<String> carParkNo);
    List<CarParkInfo> saveAll(Iterable<CarParkInfo> carParkInfos);
    long count();
    List<CarParkInfo> findAll();

    CarParkInfo save(CarParkInfo carParkInfo);
}

