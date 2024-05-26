package org.example.carpark.application.service.impl;

import org.example.carpark.application.dto.CarParkInfoResponse;
import org.example.carpark.domain.repository.CarParkAvailabilityRepository;
import org.example.carpark.domain.service.CarParkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Service implementation for car park related operations.
 */
@Service
public class CarParkServiceImpl implements CarParkService {

    private final CarParkAvailabilityRepository carParkAvailabilityRepository;

    /**
     * Constructs a CarParkServiceImpl with the specified repository.
     *
     * @param carParkAvailabilityRepository the repository for car park availability data
     */
    @Autowired
    public CarParkServiceImpl(CarParkAvailabilityRepository carParkAvailabilityRepository) {
        this.carParkAvailabilityRepository = carParkAvailabilityRepository;
    }

    /**
     * Finds all available car parks within the given latitude and longitude, paginated.
     *
     * @param latitude the latitude of the user's location
     * @param lon the longitude of the user's location
     * @param page the page number for pagination
     * @param pageSize the number of results per page
     * @return a paginated list of car park information responses
     */
    @Override
    public Page<CarParkInfoResponse> findAllCarPark(double latitude, double lon, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        return carParkAvailabilityRepository.findAvailableCarParks(latitude, lon, pageable);
    }
}