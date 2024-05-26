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

@Service
@Slf4j
public class CarParkAvailabilityServiceImpl implements CarParkAvailabilityService {

    private final CarParkAvailabilityRepository carParkAvailabilityRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final String url;

    @Autowired
    public CarParkAvailabilityServiceImpl(CarParkAvailabilityRepository carParkAvailabilityRepository, ObjectMapper objectMapper, @Value("${url.availability}") String url) {
        this.carParkAvailabilityRepository = carParkAvailabilityRepository;
        this.restTemplate = new RestTemplate();
        this.objectMapper = objectMapper;
        this.url = url;
    }

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