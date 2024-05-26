package org.example.carpark.application.service.impl;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.example.carpark.application.dto.CarParkAvailabilityResponse;
import org.example.carpark.application.dto.UpdateResponse;
import org.example.carpark.domain.model.CarParkAvailability;
import org.example.carpark.domain.repository.CarParkAvailabilityRepository;
import org.example.carpark.domain.service.CarParkAvailabilityService;
import org.example.carpark.exception.CarParkAvailabilityException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * Service implementation for fetching and storing car park availability data.
 */
@Service
@Slf4j
public class CarParkAvailabilityServiceImpl implements CarParkAvailabilityService {

    private final CarParkAvailabilityRepository carParkAvailabilityRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final String url;

    /**
     * Constructs a CarParkAvailabilityServiceImpl with the specified dependencies.
     *
     * @param carParkAvailabilityRepository the repository for storing car park availability data
     * @param objectMapper the ObjectMapper for JSON processing
     * @param url the URL for fetching car park availability data
     * @param restTemplate the RestTemplate for making HTTP requests
     */
    @Autowired
    public CarParkAvailabilityServiceImpl(CarParkAvailabilityRepository carParkAvailabilityRepository, ObjectMapper objectMapper, @Value("${url.availability}") String url, RestTemplate restTemplate) {
        this.carParkAvailabilityRepository = carParkAvailabilityRepository;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.url = url;
    }

    /**
     * Fetches car park availability data from api, transforms it, and stores it in the repository.
     *
     * @return an UpdateResponse indicating the number of records updated
     */
    @Override
    public UpdateResponse fetchAvailability() {
        try {
            String jsonResponse = restTemplate.getForObject(url, String.class);
            CarParkAvailabilityResponse response = objectMapper.readValue(jsonResponse, CarParkAvailabilityResponse.class);
            List<CarParkAvailability> availabilities = transformToDomainModel(response);

            List<CarParkAvailability> carParkAvailabilities = carParkAvailabilityRepository.saveAll(availabilities);
            return new UpdateResponse(carParkAvailabilities.size());
        } catch (Exception e) {
            log.error("Failed to fetch car park availability", e);
            throw new CarParkAvailabilityException("Failed to fetch car park availability", e);
        }
    }

    /**
     * Transforms the response from api service into a list of domain model objects.
     *
     * @param response the response from the remote service
     * @return a list of CarParkAvailability objects
     */
    private List<CarParkAvailability> transformToDomainModel(CarParkAvailabilityResponse response) {
        List<CarParkAvailability> availabilities = new ArrayList<>();
        response.getItems().forEach(item -> item.getCarparkData().forEach(data -> {
            data.getCarparkInfo().forEach(info -> {
                CarParkAvailability availability = new CarParkAvailability();
                availability.setCarParkNo(data.getCarparkNumber());
                availability.setTotalLots(info.getTotalLots());
                availability.setAvailableLots(info.getLotsAvailable());
                availabilities.add(availability);
            });
        }));
        return availabilities;
    }

}