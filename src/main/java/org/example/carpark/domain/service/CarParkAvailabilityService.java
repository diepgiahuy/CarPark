package org.example.carpark.domain.service;

import org.example.carpark.application.dto.UpdateResponse;
import org.example.carpark.domain.model.CarParkAvailability;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public abstract class CarParkAvailabilityService {
    public abstract List<CarParkAvailability> fetchAvailability();

    public abstract List<CarParkAvailability> updateAvailability(List<CarParkAvailability> carParkAvailabilities);

    @Transactional
    public UpdateResponse fetchAndUpdateAvailability() {
        List<CarParkAvailability> carParkAvailabilities = fetchAvailability();
        List<CarParkAvailability> savedRecord = updateAvailability(carParkAvailabilities);
        return new UpdateResponse(savedRecord.size());
    }
}
