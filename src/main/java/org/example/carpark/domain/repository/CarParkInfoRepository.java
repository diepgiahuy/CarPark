package org.example.carpark.domain.repository;

import org.example.carpark.domain.model.CarParkInfo;

import java.util.List;

public interface CarParkInfoRepository {
    List<CarParkInfo> saveAll(Iterable<CarParkInfo> carParkInfos);

    long count();

}

