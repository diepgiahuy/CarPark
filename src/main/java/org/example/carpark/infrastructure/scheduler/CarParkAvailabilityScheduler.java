package org.example.carpark.infrastructure.scheduler;

import org.example.carpark.domain.service.CarParkAvailabilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class CarParkAvailabilityScheduler {

    private final CarParkAvailabilityService carParkAvailabilityService;

    @Autowired
    public CarParkAvailabilityScheduler(CarParkAvailabilityService carParkAvailabilityService) {
        this.carParkAvailabilityService = carParkAvailabilityService;
    }

    @Scheduled(initialDelay = 10000, fixedRate = Long.MAX_VALUE)
    public void scheduleAvailabilityFetch() {
        carParkAvailabilityService.fetchAvailability();
    }
}