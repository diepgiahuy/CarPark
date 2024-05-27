package org.example.carpark.infrastructure.scheduler;

import org.example.carpark.domain.service.CarParkAvailabilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CarParkAvailabilityScheduler {

    private final CarParkAvailabilityService carParkAvailabilityService;

    /**
     * Constructs a new CarParkAvailabilityScheduler with the given service.
     *
     * @param carParkAvailabilityService the service to fetch car park availability
     */
    @Autowired
    public CarParkAvailabilityScheduler(CarParkAvailabilityService carParkAvailabilityService) {
        this.carParkAvailabilityService = carParkAvailabilityService;
    }

    /**
     * Schedules the availability fetch task with an initial delay of 1 minute and
     * subsequently every 10 minutes.
     * <p>
     * The method fetches the latest car park availability data using the CarParkAvailabilityService.
     */
    @Scheduled(initialDelay = 60000, fixedRate = 600000)
    public void scheduleAvailabilityFetch() {
        carParkAvailabilityService.fetchAndUpdateAvailability();
    }
}