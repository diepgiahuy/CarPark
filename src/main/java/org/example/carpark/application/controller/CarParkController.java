package org.example.carpark.application.controller;

import jakarta.validation.constraints.*;
import org.example.carpark.application.dto.UpdateResponse;
import org.example.carpark.application.dto.CarParkInfoResponse;
import org.example.carpark.domain.service.CarParkAvailabilityService;
import org.example.carpark.domain.service.CarParkService;
import org.example.carpark.infrastructure.csv.CsvCarParkDataLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/carparks")
@Validated
public class CarParkController {

    private final CarParkAvailabilityService carParkAvailabilityService;
    private final CarParkService carParkService;
    private final  CsvCarParkDataLoader carParkDataLoader;

    @Autowired
    CarParkController(CarParkAvailabilityService carParkAvailabilityService, CarParkService carParkService, CsvCarParkDataLoader carParkDataLoader) {
        this.carParkAvailabilityService = carParkAvailabilityService;
        this.carParkService = carParkService;
        this.carParkDataLoader = carParkDataLoader;
    }

    @GetMapping("/reload-csv")
    public ResponseEntity<UpdateResponse> reloadCsv() {
        return new ResponseEntity<>(carParkDataLoader.loadCarParkData(true),HttpStatus.OK);

    }

    @GetMapping("/availability")
    public ResponseEntity<UpdateResponse> fetchCarLotAvailability() {
        return new ResponseEntity<>(carParkAvailabilityService.fetchAvailability(),HttpStatus.OK);

    }
    @GetMapping("/nearest")
    public ResponseEntity<Page<CarParkInfoResponse>> findNearestCarPark(
            @RequestParam @NotNull(message = "Latitude is required")
            @DecimalMin(value = "-90.0", message = "Latitude must be between -90 and 90 degrees")
            @DecimalMax(value = "90.0", message = "Latitude must be between -90 and 90 degrees")
            Double latitude,
            @RequestParam @NotNull(message = "Longitude is required")
            @DecimalMin(value = "-180", message = "Longitude must be between -180 and 180 degrees")
            @DecimalMax(value = "180", message = "Longitude must be between -180 and 180 degrees")
            Double longitude,
            @RequestParam @NotNull(message = "Page is required")
            @Min(value = 0, message = "Page must be possitive number")
            Integer page,
            @RequestParam(name = "per_page") @NotNull(message = "Per page is required")
            @Min(value = 1, message = "Per page must be greater than 0")
            @Max(value = 100, message = "Per page must be less than or equal to 100")
            Integer perPage) {

        return new ResponseEntity<>( carParkService.findAllCarPark(
                latitude,
                longitude,
                page,
                perPage
        ), HttpStatus.OK);
    }
}