package org.example.carpark.application.service.impl;

import org.example.carpark.application.dto.CarParkInfoResponse;
import org.example.carpark.domain.repository.CarParkAvailabilityRepository;
import org.example.carpark.domain.service.CarParkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class CarParkServiceImpl implements CarParkService {

    private final CarParkAvailabilityRepository carParkAvailabilityRepository;


    @Autowired
    public CarParkServiceImpl(CarParkAvailabilityRepository carParkAvailabilityRepository) {
        this.carParkAvailabilityRepository = carParkAvailabilityRepository;
    }

    @Override
    public Page<CarParkInfoResponse> findAllCarPark(double latitude, double lon, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        return carParkAvailabilityRepository.findAvailableCarParks(latitude, lon, pageable);
    }
}