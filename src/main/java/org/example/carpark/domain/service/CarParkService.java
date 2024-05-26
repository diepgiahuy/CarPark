package org.example.carpark.domain.service;

import org.example.carpark.application.dto.CarParkInfoResponse;
import org.springframework.data.domain.Page;

public interface CarParkService {
    Page<CarParkInfoResponse> findAllCarPark(double lat, double lon, int page, int pageSize);
}
