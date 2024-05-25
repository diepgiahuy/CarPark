package org.example.carpark.application.service.impl;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.example.carpark.application.dto.CarParkAvailabilityResponse;
import org.example.carpark.exception.CarParkAvailabilityException;
import org.example.carpark.domain.model.CarParkAvailability;
import org.example.carpark.domain.repository.CarParkRepository;
import org.example.carpark.domain.service.CarParkAvailabilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CarParkAvailabilityServiceImpl implements CarParkAvailabilityService {

    private final CarParkRepository carParkAvailabilityRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    private final String url;


    @Autowired
    public CarParkAvailabilityServiceImpl(CarParkRepository carParkAvailabilityRepository, ObjectMapper objectMapper, @Value("${url.availability}") String url) {
        this.carParkAvailabilityRepository = carParkAvailabilityRepository;
        this.restTemplate = new RestTemplate();
        this.objectMapper = objectMapper;
        this.url = url;
    }

    @Override
    @Transactional
    public Boolean fetchAvailability() {
        String jsonResponse = restTemplate.getForObject(url, String.class);

        try {
            CarParkAvailabilityResponse response = objectMapper.readValue(jsonResponse, CarParkAvailabilityResponse.class);
            List<CarParkAvailability> availabilities = transformToDomainModel(response);
            List<String> carParkNo = availabilities.stream()
                    .map(CarParkAvailability::getCarParkNo)
                    .collect(Collectors.toList());

            List<CarParkAvailability> existingRecords = carParkAvailabilityRepository.findAllByCarParkNoIn(carParkNo);
            Map<String, CarParkAvailability> existingMap = new HashMap<>();
            for (CarParkAvailability record : existingRecords) {
                existingMap.put(record.getCarParkNo(), record);
            }
            List<CarParkAvailability> toSave = new ArrayList<>();
            for (CarParkAvailability availability : availabilities) {
                if (existingMap.containsKey(availability.getCarParkNo())) {
                    CarParkAvailability existing = existingMap.get(availability.getCarParkNo());
                    if (!existing.equals(availability)) {
                        existing.setTotalLots(availability.getTotalLots());
                        existing.setAvailableLots(availability.getAvailableLots());
                        existing.setCarParkNo(availability.getCarParkNo());
                        toSave.add(existing);
                    }
                } else {
                    toSave.add(availability);
                }
            }
            // Save all new and updated records in batches
            int batchSize = 500; // Define an appropriate batch size
            for (int i = 0; i < toSave.size(); i += batchSize) {
                int end = Math.min(toSave.size(), i + batchSize);
                carParkAvailabilityRepository.saveAll(toSave.subList(i, end));
            }
        } catch (Exception e) {
            throw new CarParkAvailabilityException("Failed to fetch car park availability", e);
        }
        return true;
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