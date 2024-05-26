package org.example.carpark.domain.service;

import org.example.carpark.application.dto.CarParkResponse;
import org.springframework.data.domain.Page;

public interface CarParkService {
    Page<CarParkResponse> findAllCarPark(double lat, double lon, int page, int pageSize);
}
