package org.example.carpark.application.service.impl;

import org.example.carpark.TestDataUtil;
import org.example.carpark.application.dto.CarParkInfoResponse;
import org.example.carpark.domain.repository.CarParkAvailabilityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

public class CarParkServiceImplTest {

    @Mock
    private CarParkAvailabilityRepository carParkAvailabilityRepository;

    @InjectMocks
    private CarParkServiceImpl carParkService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testFindAllCarPark() {
        double latitude = 1.1;
        double longitude = 1.1;
        int page = 0;
        int pageSize = 10;
        Pageable pageable = PageRequest.of(page, pageSize);

        CarParkInfoResponse carPark1 = TestDataUtil.createCarParkInfoResponse("Car Park 1", 1.1, 1.1, 100, 50, 0.1);
        CarParkInfoResponse carPark2 = TestDataUtil.createCarParkInfoResponse("Car Park 2", 1.2, 1.2, 150, 75, 0.2);
        List<CarParkInfoResponse> carParks = Arrays.asList(carPark1, carPark2);
        Page<CarParkInfoResponse> carParkPage = new PageImpl<>(carParks, pageable, carParks.size());

        when(carParkAvailabilityRepository.findAvailableCarParks(latitude, longitude, pageable)).thenReturn(carParkPage);

        Page<CarParkInfoResponse> result = carParkService.findAllCarPark(latitude, longitude, page, pageSize);

        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals(carPark1, result.getContent().get(0));
        assertEquals(carPark2, result.getContent().get(1));

        verify(carParkAvailabilityRepository, times(1)).findAvailableCarParks(latitude, longitude, pageable);
    }

    @Test
    public void testFindAllCarPark_NoCarParksAvailable() {
        double latitude = 1.1;
        double longitude = 1.1;
        int page = 0;
        int pageSize = 10;
        Pageable pageable = PageRequest.of(page, pageSize);

        List<CarParkInfoResponse> carParks = Collections.emptyList();
        Page<CarParkInfoResponse> carParkPage = new PageImpl<>(carParks, pageable, carParks.size());

        when(carParkAvailabilityRepository.findAvailableCarParks(latitude, longitude, pageable)).thenReturn(carParkPage);

        Page<CarParkInfoResponse> result = carParkService.findAllCarPark(latitude, longitude, page, pageSize);

        assertNotNull(result);
        assertEquals(0, result.getTotalElements());

        verify(carParkAvailabilityRepository, times(1)).findAvailableCarParks(latitude, longitude, pageable);
    }

    @Test
    public void testFindAllCarPark_PageBoundary() {
        double latitude = 1.1;
        double longitude = 1.1;
        int page = 1; // Requesting a page number that is out of bounds
        int pageSize = 10;
        Pageable pageable = PageRequest.of(page, pageSize);

        List<CarParkInfoResponse> carParks = Collections.emptyList();
        Page<CarParkInfoResponse> carParkPage = new PageImpl<>(carParks, pageable, carParks.size());

        when(carParkAvailabilityRepository.findAvailableCarParks(latitude, longitude, pageable)).thenReturn(carParkPage);

        Page<CarParkInfoResponse> result = carParkService.findAllCarPark(latitude, longitude, page, pageSize);

        assertNotNull(result);
        assertEquals(0, result.getTotalElements());

        verify(carParkAvailabilityRepository, times(1)).findAvailableCarParks(latitude, longitude, pageable);
    }

    @Test
    public void testFindAllCarPark_DifferentPageSizes() {
        double latitude = 1.1;
        double longitude = 1.1;
        int page = 0;
        int pageSize = 1;
        Pageable pageable = PageRequest.of(page, pageSize);

        CarParkInfoResponse carPark1 = TestDataUtil.createCarParkInfoResponse("Car Park 1", 1.1, 1.1, 100, 50, 0.1);
        List<CarParkInfoResponse> carParks = Collections.singletonList(carPark1);
        Page<CarParkInfoResponse> carParkPage = new PageImpl<>(carParks, pageable, carParks.size());

        when(carParkAvailabilityRepository.findAvailableCarParks(latitude, longitude, pageable)).thenReturn(carParkPage);

        Page<CarParkInfoResponse> result = carParkService.findAllCarPark(latitude, longitude, page, pageSize);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(carPark1, result.getContent().get(0));

        verify(carParkAvailabilityRepository, times(1)).findAvailableCarParks(latitude, longitude, pageable);
    }

}
