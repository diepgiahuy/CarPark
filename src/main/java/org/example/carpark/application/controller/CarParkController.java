package org.example.carpark.application.controller;

import org.example.carpark.domain.service.CarParkAvailabilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/carparks")
@Validated
public class CarParkController {

    private final CarParkAvailabilityService carParkAvailabilityService;

    @Autowired
    CarParkController(CarParkAvailabilityService carParkAvailabilityService) {
        this.carParkAvailabilityService = carParkAvailabilityService;
    }
    @GetMapping("/availability")
    public Boolean fetchCarLotAvailability() {
        return carParkAvailabilityService.fetchAvailability();
    }
}