package org.example.carpark.domain.repository;

import org.example.carpark.application.dto.CarParkInfoResponse;
import org.example.carpark.domain.model.CarParkAvailability;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CarParkAvailabilityRepository {
    Page<CarParkInfoResponse> findAvailableCarParks(double latitude,double longitude, Pageable pageable);

    List<CarParkAvailability> saveAll(Iterable<CarParkAvailability> carParkInfos);


}

