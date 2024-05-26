package org.example.carpark.domain.service;

import org.example.carpark.application.dto.CarParkAvailabilityUpdateResponse;

public interface CarParkAvailabilityService {
    CarParkAvailabilityUpdateResponse fetchAvailability();
}
