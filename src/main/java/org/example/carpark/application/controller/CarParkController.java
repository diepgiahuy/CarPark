package org.example.carpark.application.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.*;
import org.example.carpark.application.dto.UpdateResponse;
import org.example.carpark.application.dto.CarParkInfoResponse;
import org.example.carpark.domain.service.CarParkAvailabilityService;
import org.example.carpark.domain.service.CarParkService;
import org.example.carpark.exception.CarParkAvailabilityException;
import org.example.carpark.exception.GlobalExceptionHandler;
import org.example.carpark.infrastructure.csv.CsvCarParkDataLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/carparks")
@Validated
@Tag(name = "CarParkAPI", description = "Operations related to car park data")
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

    @Operation(summary = "Reload car park data from CSV", description = "Forces a reload of car park data from the CSV file")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "CSV data reloaded successfully",
                    content = @Content(schema =  @Schema(implementation = UpdateResponse.class))),
            @ApiResponse(responseCode = "500", description = "Failed to fetch car park availability",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RuntimeException.class)))
    })
    @GetMapping("/reload-csv")
    public ResponseEntity<UpdateResponse> reloadCsv() {
        return new ResponseEntity<>(carParkDataLoader.loadCarParkData(true),HttpStatus.OK);

    }

    @Operation(summary = "Fetch car park availability", description = "Fetches the latest car park lot availability data")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Car park availability data fetched successfully",
            content = @Content(schema =  @Schema(implementation = UpdateResponse.class))),
            @ApiResponse(responseCode = "500", description = "Failed to fetch car park availability",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CarParkAvailabilityException.class)))
    })
    @GetMapping("/availability")
    public ResponseEntity<UpdateResponse> fetchCarLotAvailability() {
        return new ResponseEntity<>(carParkAvailabilityService.fetchAvailability(),HttpStatus.OK);

    }
    @Operation(summary = "Get nearest car parks", description = "Returns a paginated list of nearest car parks with available parking lots")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(implementation = CarParkInfoResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid parameters supplied",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = MissingServletRequestParameterException.class)))
    })

    @GetMapping("/nearest")
    public ResponseEntity<Page<CarParkInfoResponse>> findNearestCarPark(
            @Parameter(description = "Latitude of the user's location", required = true)
            @RequestParam @NotNull(message = "Latitude is required")
            @DecimalMin(value = "-90.0", message = "Latitude must be between -90 and 90 degrees")
            @DecimalMax(value = "90.0", message = "Latitude must be between -90 and 90 degrees")
            Double latitude,

            @Parameter(description = "Longitude of the user's location", required = true)
            @RequestParam @NotNull(message = "Longitude is required")
            @DecimalMin(value = "-180", message = "Longitude must be between -180 and 180 degrees")
            @DecimalMax(value = "180", message = "Longitude must be between -180 and 180 degrees")
            Double longitude,

            @Parameter(description = "Page number for pagination", required = true)
            @RequestParam @NotNull(message = "Page is required")
            @Min(value = 0, message = "Page must be possitive number")
            Integer page,

            @Parameter(description = "Number of results per page", required = true)
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